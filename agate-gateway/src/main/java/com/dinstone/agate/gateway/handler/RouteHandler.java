/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.agate.gateway.handler;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.http.QueryCoder;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.BackendOptions;
import com.dinstone.agate.gateway.options.ParamOptions;
import com.dinstone.agate.gateway.options.ParamType;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.RoutingContext;

public class RouteHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(RouteHandler.class);

    private BackendOptions backendOptions;

    private HttpClient httpClient;

    private ApiOptions apiOptions;

    private int count;

    public RouteHandler(ApiOptions api, boolean b, HttpClient httpClient) {
        this.apiOptions = api;
        this.httpClient = httpClient;

        backendOptions = api.getBackend();
    }

    @Override
    public void handle(RoutingContext rc) {
        try {
            route(rc);
        } catch (Exception e) {
            LOG.error("route backend service error", e);
            rc.response().setStatusCode(500).end("route backend service error");
        }
    }

    private void route(RoutingContext rc) {
        HttpServerRequest feRequest = rc.request();
        HttpServerResponse feResponse = rc.response();

        //
        feResponse.endHandler(v -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("frontend status {} {}", feRequest.path(), feResponse.getStatusCode());
            }
        });

        Map<String, String> pathParams = new HashMap<>();
        MultiMap queryParams = new CaseInsensitiveHeaders();
        MultiMap headerParams = new CaseInsensitiveHeaders();

        // load param from request
        List<ParamOptions> params = backendOptions.getParams();
        if (params != null) {
            for (ParamOptions param : params) {
                if (ParamType.PATH == param.getBeParamType()) {
                    String value = findParamValue(rc, param);
                    pathParams.put(param.getBeParamName(), value);
                } else if (ParamType.QUERY == param.getBeParamType()) {
                    String value = findParamValue(rc, param);
                    queryParams.add(param.getBeParamName(), value);
                } else if (ParamType.HEADER == param.getBeParamType()) {
                    String value = findParamValue(rc, param);
                    headerParams.add(param.getBeParamName(), value);
                }
            }
        }

        // locate url
        String requestUrl = locateUrl(feRequest);
        // replace param for url
        for (Entry<String, String> e : pathParams.entrySet()) {
            requestUrl = requestUrl.replace(":" + e.getKey(), e.getValue());
        }

        // set query params
        if (queryParams.size() > 0) {
            QueryCoder queryCoder = new QueryCoder(requestUrl);
            for (Entry<String, String> e : queryParams) {
                queryCoder.addParam(e.getKey(), e.getValue());
            }
            requestUrl = queryCoder.uri();
        }

        // create backend request
        HttpClientRequest beRequest = httpClient.requestAbs(method(feRequest.method()), requestUrl);
        // set headers
        beRequest.headers().addAll(feRequest.headers());
        if (headerParams.size() > 0) {
            for (Entry<String, String> e : headerParams) {
                beRequest.headers().set(e.getKey(), e.getValue());
            }
        }

        // exception handler
        beRequest.exceptionHandler(t -> {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("request backend service error", t);
                }
                if (t instanceof ConnectException || t instanceof TimeoutException) {
                    feResponse.setStatusCode(504).end("backend service unavailable");
                } else {
                    feResponse.setStatusCode(500).end("request backend service error");
                }
            } catch (Exception e) {
                LOG.warn("unkown error", e);
            }
        });

        // response handler
        beRequest.setHandler(ar -> {
            if (ar.succeeded()) {
                HttpClientResponse beResponse = ar.result();

                feResponse.setStatusCode(beResponse.statusCode());
                feResponse.setChunked(true).headers().addAll(beResponse.headers());

                Pump respPump = Pump.pump(beResponse, feResponse).start();
                beResponse.exceptionHandler(e -> {
                    LOG.error("backend response is error", e);
                    respPump.stop();
                    feResponse.end();
                }).endHandler(v -> {
                    feResponse.end();
                });
            } else {
                LOG.error("backend response is error", ar.cause());
                feResponse.setStatusCode(500).end("request backend service error");
            }
        });

        // transport body and send request
        if (hasBody(beRequest.method())) {
            beRequest.setChunked(true);
            Pump reqPump = Pump.pump(feRequest, beRequest).start();
            feRequest.exceptionHandler(e -> {
                LOG.error("API:" + apiOptions.getApiName() + ", pump backedn request error.", e);
                reqPump.stop();
                beRequest.end();
            }).endHandler(v -> {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("body transport finish");
                }
                beRequest.end();
            });
        } else {
            beRequest.end();
        }
    }

    private String findParamValue(RoutingContext rc, ParamOptions param) {
        if (ParamType.PATH == param.getFeParamType()) {
            return rc.pathParam(param.getFeParamName());
        } else if (ParamType.QUERY == param.getFeParamType()) {
            return rc.request().getParam(param.getFeParamName());
        } else if (ParamType.HEADER == param.getFeParamType()) {
            return rc.request().getHeader(param.getFeParamName());
        }

        return null;
    }

    private boolean hasBody(HttpMethod method) {
        return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE
                || method == HttpMethod.PATCH || method == HttpMethod.TRACE;
    }

    private String locateUrl(HttpServerRequest inRequest) {
        List<String> urls = backendOptions.getUrls();
        if (count++ < 0) {
            count = 0;
        }

        int index = count % urls.size();
        return urls.get(index);
    }

    private HttpMethod method(HttpMethod httpMethod) {
        String method = backendOptions.getMethod();
        if (method != null) {
            return HttpMethod.valueOf(method.toUpperCase());
        }
        return httpMethod;
    }

}
