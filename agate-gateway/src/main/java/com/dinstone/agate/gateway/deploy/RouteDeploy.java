/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
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

package com.dinstone.agate.gateway.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.options.GatewayOptions;
import com.dinstone.agate.gateway.options.RouteOptions;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

public class RouteDeploy {

    private static final Logger LOG = LoggerFactory.getLogger(RouteDeploy.class);

    private GatewayOptions gatewayOptions;

    private RouteOptions routeOptions;

    private HttpClient httpClient;

    private CircuitBreaker circuitBreaker;

    public RouteDeploy(GatewayOptions gatewayOptions, RouteOptions routeOptions) {
        this.gatewayOptions = gatewayOptions;
        this.routeOptions = routeOptions;
    }

    public String getRoute() {
        return routeOptions.getRoute();
    }

    public RouteOptions getRouteOptions() {
        return routeOptions;
    }

    public GatewayOptions getGatewayOptions() {
        return gatewayOptions;
    }

    public void destory() {
        synchronized (this) {
            if (httpClient != null) {
                httpClient.close();
            }
            if (circuitBreaker != null) {
                circuitBreaker.close();
            }
        }
    }

    public HttpClient createHttpClient(Vertx vertx) {
        synchronized (this) {
            if (httpClient == null) {
                HttpClientOptions clientOptions = gatewayOptions.getClientOptions();
                if (clientOptions == null) {
                    clientOptions = new HttpClientOptions();
                    clientOptions.setKeepAlive(true);
                    clientOptions.setConnectTimeout(2000);
                    // clientOptions.setMaxWaitQueueSize(1000);
                    clientOptions.setIdleTimeout(10);
                    clientOptions.setMaxPoolSize(100);
                    // clientOptions.setTracingPolicy(TracingPolicy.PROPAGATE);
                }
                httpClient = vertx.createHttpClient(clientOptions);
            }
            return httpClient;
        }
    }

    public CircuitBreaker createCircuitBreaker(Vertx vertx) {
        synchronized (this) {
            if (circuitBreaker == null) {
                CircuitBreakerOptions cbOptions = new CircuitBreakerOptions();
                // cbOptions.setFailuresRollingWindow(10000);
                cbOptions.setMaxFailures(10);
                // If an action is not completed before this timeout, the action is considered as a failure.
                cbOptions.setTimeout(3000);
                // does not succeed in time
                cbOptions.setFallbackOnFailure(false);
                // time spent in open state before attempting to re-try
                cbOptions.setResetTimeout(5000);
                this.circuitBreaker = CircuitBreaker.create(routeOptions.getRoute(), vertx, cbOptions)
                    .openHandler(v -> {
                        LOG.debug("circuit breaker {} open", circuitBreaker.name());
                    }).closeHandler(v -> {
                        LOG.debug("circuit breaker {} close", circuitBreaker.name());
                    }).halfOpenHandler(v -> {
                        LOG.debug("circuit breaker {} half", circuitBreaker.name());
                    });
            }
            return circuitBreaker;
        }
    }

}
