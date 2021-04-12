/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import com.dinstone.agate.gateway.context.ContextConstants;
import com.dinstone.agate.gateway.http.HttpUtil;
import com.dinstone.agate.gateway.http.QueryCoder;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.RoutingOptions;
import com.dinstone.agate.gateway.options.ParamOptions;
import com.dinstone.agate.gateway.options.ParamType;
import com.dinstone.agate.gateway.spi.RouteHandler;
import com.dinstone.agate.tracing.HttpClientTracing;
import com.dinstone.agate.tracing.ZipkinTracer;
import io.vertx.core.MultiMap;
import io.vertx.core.http.*;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http route and proxy.
 * 
 * @author dinstone
 *
 */
public class ProxyInvokeHandler implements RouteHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyInvokeHandler.class);

    private final HttpClient httpClient;

    private final ApiOptions apiOptions;

    private final ZipkinTracer zipkinTracer;

    private final RoutingOptions backendOptions;

    private int count;

    public ProxyInvokeHandler(ApiOptions apiOptions, HttpClient httpClient, ZipkinTracer zipkinTracer) {
        this.apiOptions = apiOptions;
        this.httpClient = httpClient;
        this.zipkinTracer = zipkinTracer;
        this.backendOptions = apiOptions.getRouting();
    }

    @Override
    public void handle(RoutingContext rc) {
        try {
            routing(rc);
        } catch (Exception e) {
            rc.fail(500, new RuntimeException("route handle error: " + e.getMessage(), e));
        }
    }

    private void routing(RoutingContext rc) {
        Map<String, String> pathParams = new HashMap<>();
        MultiMap queryParams = MultiMap.caseInsensitiveMultiMap();
        MultiMap headerParams = MultiMap.caseInsensitiveMultiMap();

        // parse params from frontend request
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

        HttpServerRequest feRequest = rc.request();
        // locate url
        String requestUrl = loadbalanceUrl(feRequest);
        // replace param for url
        if (!pathParams.isEmpty()) {
            for (Entry<String, String> e : pathParams.entrySet()) {
                requestUrl = requestUrl.replace(":" + e.getKey(), e.getValue() == null ? "" : e.getValue());
            }
        }
        // set query params
        if (!queryParams.isEmpty()) {
            QueryCoder queryCoder = new QueryCoder(requestUrl);
            for (Entry<String, String> e : queryParams) {
                queryCoder.addParam(e.getKey(), e.getValue());
            }
            requestUrl = queryCoder.uri();
        } else if (feRequest.query() != null) {
            // copy query frontend params to backend params
            QueryCoder queryCoder = new QueryCoder(requestUrl);
            for (Entry<String, String> kve : rc.queryParams().entries()) {
                queryCoder.addParam(kve.getKey(), kve.getValue());
            }
            requestUrl = queryCoder.uri();
        }

        // create backend request
        RequestOptions options = new RequestOptions();
        // set url
        options.setAbsoluteURI(requestUrl);
        // set method
        options.setMethod(method(feRequest.method()));
        // set timeout
        if (backendOptions.getTimeout() > 0) {
            options.setTimeout(backendOptions.getTimeout());
        }
        // set headers
        for (Entry<String, String> e : feRequest.headers()) {
            if (e.getKey().equalsIgnoreCase(HttpHeaders.HOST.toString())) {
                continue;
            }
            options.addHeader(e.getKey(), e.getValue());
        }
        if (headerParams.size() > 0) {
            for (Entry<String, String> e : headerParams) {
                options.addHeader(e.getKey(), e.getValue());
            }
        }
        // create http request
        httpClient.request(options).onSuccess(r -> {
            HttpClientRequest beRequest = r;
            if (zipkinTracer != null) {
                trace(rc, beRequest);
            } else {
                common(rc, beRequest);
            }

            // transport body and send request
            if (HttpUtil.hasBody(beRequest.getMethod())) {
                Pump fe2bePump = Pump.pump(feRequest, beRequest).start();
                feRequest.exceptionHandler(e -> {
                    LOG.error("API:" + apiOptions.getApiName() + ", pump backend request error.", e);
                    fe2bePump.stop();
                    beRequest.end();
                }).endHandler(v -> {
                    beRequest.end();
                });
            } else {
                beRequest.end();
            }
        }).onFailure(t -> {
            rc.fail(503, t);
        });
    }

    private void common(RoutingContext rc, HttpClientRequest beRequest) {
        // response handler
        beRequest.response().onComplete(ar -> {
            if (ar.succeeded()) {
                rc.put(ContextConstants.BACKEND_RESPONSE, ar.result());
                rc.next();
            } else {
                rc.fail(503, ar.cause());
            }
        });
    }

    void trace(RoutingContext rc, HttpClientRequest beRequest) {
        // tracing
        HttpClientTracing tracing = zipkinTracer.httpClientTracing().start(beRequest);
        tracing.tag("api.name", apiOptions.getApiName()).tag("app.name", apiOptions.getGateway());
        // response handler
        beRequest.response().onComplete(ar -> {
            if (ar.succeeded()) {
                rc.put(ContextConstants.BACKEND_TRACING, tracing);
                rc.put(ContextConstants.BACKEND_RESPONSE, ar.result());
                rc.next();
            } else {
                tracing.failure(ar.cause());
                rc.fail(503, ar.cause());
            }
        });
    }

    private String findParamValue(RoutingContext rc, ParamOptions param) {
        if (ParamType.PATH == param.getFeParamType()) {
            return rc.pathParam(param.getFeParamName());
        } else if (ParamType.QUERY == param.getFeParamType()) {
            return rc.queryParams().get(param.getFeParamName());
        } else if (ParamType.HEADER == param.getFeParamType()) {
            return rc.request().getHeader(param.getFeParamName());
        }

        return null;
    }

    private String loadbalanceUrl(HttpServerRequest inRequest) {
        List<String> urls = backendOptions.getUrls();
        if (count++ < 0) {
            count = 0;
        }

        int index = count % urls.size();
        return urls.get(index);
    }

    private HttpMethod method(HttpMethod httpMethod) {
        String method = backendOptions.getMethod();
        if (method != null && !method.isEmpty()) {
            return HttpMethod.valueOf(method.toUpperCase());
        }
        return httpMethod;
    }

}
