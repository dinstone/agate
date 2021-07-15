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
package com.dinstone.agate.gateway.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.GatewayOptions;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

public class ApiDeploy {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDeploy.class);

    private GatewayOptions gatewayOptions;

    private ApiOptions apiOptions;

    private HttpClient httpClient;

    private CircuitBreaker circuitBreaker;

    public ApiDeploy(GatewayOptions gatewayOptions, ApiOptions apiOptions) {
        this.gatewayOptions = gatewayOptions;
        this.apiOptions = apiOptions;
    }

    public String getApiName() {
        return apiOptions.getApiName();
    }

    public ApiOptions getApiOptions() {
        return apiOptions;
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
                    clientOptions.setConnectTimeout(2000);
                    clientOptions.setMaxWaitQueueSize(5);
                    clientOptions.setIdleTimeout(10);
                    clientOptions.setMaxPoolSize(5);
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
                cbOptions.setTimeout(1000);
                // does not succeed in time
                cbOptions.setFallbackOnFailure(false);
                // time spent in open state before attempting to re-try
                cbOptions.setResetTimeout(10000);
                this.circuitBreaker = CircuitBreaker.create(apiOptions.getApiName(), vertx, cbOptions)
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
