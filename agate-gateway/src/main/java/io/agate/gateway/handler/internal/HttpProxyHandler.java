/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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
package io.agate.gateway.handler.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.ContextConstants;
import io.agate.gateway.handler.OrderedHandler;
import io.agate.gateway.http.HttpUtil;
import io.agate.gateway.http.QueryCoder;
import io.agate.gateway.options.BackendOptions;
import io.agate.gateway.options.ParamOptions;
import io.agate.gateway.options.ParamType;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.service.ConsulServiceAddressSupplier;
import io.agate.gateway.service.FixedServiceAddressSupplier;
import io.agate.gateway.service.Loadbalancer;
import io.agate.gateway.service.RoundRobinLoadBalancer;
import io.agate.gateway.service.ServiceAddressSupplier;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.Pipe;
import io.vertx.ext.web.RoutingContext;

/**
 * http route and proxy.
 *
 * @author dinstone
 */
public class HttpProxyHandler extends OrderedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpProxyHandler.class);

    private static final String ALL_PATH = "/*";

    private final HttpClient httpClient;

    private final Loadbalancer loadbalancer;

    private final RouteOptions routeOptions;

    private final BackendOptions backendOptions;

    private ServiceAddressSupplier serviceSupplier;

    public HttpProxyHandler(Vertx vertx, RouteOptions routeOptions, PluginOptions pluginOptions) {
        super(500);

        this.routeOptions = routeOptions;
        this.loadbalancer = createLoadbalancer(vertx, routeOptions);
        this.httpClient = createHttpClient(vertx, routeOptions);
        this.backendOptions = routeOptions.getBackend();
    }

    private Loadbalancer createLoadbalancer(Vertx vertx, RouteOptions routeOptions) {
        // service discovery
        if (routeOptions.getBackend().getType() == 1) {
            serviceSupplier = new ConsulServiceAddressSupplier(vertx, routeOptions);
        } else {
            serviceSupplier = new FixedServiceAddressSupplier(vertx, routeOptions);
        }
        return new RoundRobinLoadBalancer(routeOptions, serviceSupplier);
    }

    private HttpClient createHttpClient(Vertx vertx, RouteOptions routeOptions) {
        HttpClientOptions clientOptions;
        JsonObject config = routeOptions.getBackend().getConnection();
        if (config != null) {
            clientOptions = new HttpClientOptions(config);
        } else {
            clientOptions = new HttpClientOptions();
            clientOptions.setKeepAlive(true);
            clientOptions.setConnectTimeout(2000);
            // clientOptions.setMaxWaitQueueSize(1000);
            clientOptions.setIdleTimeout(10);
            clientOptions.setMaxPoolSize(100);
            // clientOptions.setTracingPolicy(TracingPolicy.PROPAGATE);
        }
        return vertx.createHttpClient(clientOptions);
    }

    @Override
    public void destroy() {
        if (httpClient != null) {
            httpClient.close();
        }
        if (serviceSupplier != null) {
            serviceSupplier.close();
        }
    }

    @Override
    public void handle(RoutingContext rc) {
        HttpServerRequest feRequest = rc.request();
        // pause frontend request
        Pipe<Buffer> bodyPipe = createPipe(feRequest);

        // locate url
        String backendAddr = loadbalancer.choose();
        if (backendAddr == null) {
            // Service Unavailable
            rc.fail(501, new IllegalStateException("No servers available for service"));
            return;
        }

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

        // replace param for path
        String backendPath = backendPath(feRequest.path());
        if (!pathParams.isEmpty()) {
            for (Entry<String, String> e : pathParams.entrySet()) {
                backendPath = backendPath.replace(":" + e.getKey(), e.getValue() == null ? "" : e.getValue());
            }
        }
        // set query params for path
        if (!queryParams.isEmpty()) {
            QueryCoder queryCoder = new QueryCoder(backendPath);
            for (Entry<String, String> e : queryParams) {
                queryCoder.addParam(e.getKey(), e.getValue());
            }
            backendPath = queryCoder.uri();
        } else if (feRequest.query() != null) {
            // copy query frontend params to backend params
            QueryCoder queryCoder = new QueryCoder(backendPath);
            for (Entry<String, String> kve : rc.queryParams().entries()) {
                queryCoder.addParam(kve.getKey(), kve.getValue());
            }
            backendPath = queryCoder.uri();
        }

        // create backend request options
        RequestOptions options = new RequestOptions();
        // set url
        options.setAbsoluteURI(backendUrl(backendAddr, backendPath));
        // set method
        options.setMethod(backendMethod(feRequest.method()));
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

        // create backend request and send body
        httpClient.request(options).onSuccess(beRequest -> {
            // response handler
            beRequest.response().onComplete(ar -> {
                if (ar.succeeded()) {
                    HttpClientResponse beResponse = ar.result();
                    rc.put(ContextConstants.BACKEND_REQUEST, beRequest);
                    rc.put(ContextConstants.BACKEND_RESPONSE, beResponse);
                    rc.next();
                } else {
                    // Service Unavailable
                    rc.fail(503, ar.cause());
                }
            });

            // transport body and send request
            if (bodyPipe != null) {
                bodyPipe.to(beRequest).onFailure(e -> {
                    // service unavailable
                    LOG.warn("route:" + routeOptions.getRoute() + ", pump backend request error.", e);
                    beRequest.reset();
                    rc.fail(503, e);
                });
            } else {
                beRequest.end();
            }
        }).onFailure(t -> {
            rc.fail(502, t);// bad gateway
        });

    }

    private String backendUrl(String backendAddr, String backendPath) {
        if (backendAddr.endsWith("/")) {
            backendAddr = backendAddr.substring(0, backendAddr.length() - 1);
        }
        if (!backendPath.startsWith("/")) {
            backendPath = "/" + backendPath;
        }
        return backendAddr + backendPath;
    }

    private String backendPath(String frontendPath) {
        String backendPath = backendOptions.getPath();
        if (backendPath == null || backendPath.isEmpty() || ALL_PATH.equals(backendPath)) {
            return frontendPath;
        }
        return backendPath;
    }

    private HttpMethod backendMethod(HttpMethod httpMethod) {
        String method = backendOptions.getMethod();
        if (method != null && !method.isEmpty()) {
            return HttpMethod.valueOf(method.toUpperCase());
        }
        return httpMethod;
    }

    private Pipe<Buffer> createPipe(HttpServerRequest feRequest) {
        // transport body and send request
        if (HttpUtil.hasBody(feRequest.method())) {
            return feRequest.pipe();
        }

        return null;
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

}
