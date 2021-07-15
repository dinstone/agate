/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.ContextConstants;
import com.dinstone.agate.gateway.deploy.ApiDeploy;
import com.dinstone.agate.gateway.http.HttpUtil;
import com.dinstone.agate.gateway.http.QueryCoder;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.ParamOptions;
import com.dinstone.agate.gateway.options.ParamType;
import com.dinstone.agate.gateway.options.RoutingOptions;
import com.dinstone.agate.gateway.spi.RouteHandler;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.RoutingContext;

/**
 * http route and proxy.
 * 
 * @author dinstone
 *
 */
public class HttpProxyHandler implements RouteHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpProxyHandler.class);

    private final HttpClient httpClient;

    private final ApiOptions apiOptions;

    private final RoutingOptions backendOptions;

    private CircuitBreaker circuitBreaker;

    private int count;

    public HttpProxyHandler(ApiOptions apiOptions, HttpClient httpClient, CircuitBreaker circuitBreaker) {
        this.apiOptions = apiOptions;
        this.httpClient = httpClient;
        this.circuitBreaker = circuitBreaker;
        this.backendOptions = apiOptions.getRouting();
    }

    @Override
    public void handle(RoutingContext rc) {
        if (circuitBreaker != null) {
            circuitBreaker.<Void> executeWithFallback(promise -> {
                routing(rc).onComplete(promise);
            }, t -> {
                rc.fail(504, t); // gateway time-out
                return null;
            });
        } else {
            routing(rc);
        }
    }

    private Future<Void> routing(RoutingContext rc) {
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

        Promise<Void> promise = Promise.promise();

        // create http request
        httpClient.request(options).onSuccess(beRequest -> {
            // response handler
            beRequest.response().onComplete(ar -> {
                if (ar.succeeded()) {
                    rc.put(ContextConstants.BACKEND_RESPONSE, ar.result());
                    rc.next();
                    promise.complete();
                } else {
                    // tracing.failure(ar.cause());
                    rc.fail(503, ar.cause()); // Service Unavailable
                    promise.fail(ar.cause());
                }
            });

            // transport body and send request
            if (HttpUtil.hasBody(beRequest.getMethod())) {
                Pump fe2bePump = Pump.pump(feRequest, beRequest).start();
                feRequest.exceptionHandler(e -> {
                    LOG.error("API:" + apiOptions.getApiName() + ", pump backend request error.", e);
                    fe2bePump.stop();
                    beRequest.reset();
                }).endHandler(v -> {
                    beRequest.end();
                });
            } else {
                beRequest.end();
            }
        }).onFailure(t -> {
            rc.fail(502, t);// bad gateway
            promise.fail(t);
        });

        return promise.future();
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

    public static Handler<RoutingContext> create(ApiDeploy deploy, Vertx vertx) {
        HttpClient httpClient = deploy.createHttpClient(vertx);
        CircuitBreaker circuitBreaker = deploy.createCircuitBreaker(vertx);
        return new HttpProxyHandler(deploy.getApiOptions(), httpClient, circuitBreaker);
    }

}
