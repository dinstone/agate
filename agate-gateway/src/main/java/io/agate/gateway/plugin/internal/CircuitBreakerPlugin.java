/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.gateway.plugin.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.handler.internal.CircuitBreakerHandler;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;

public class CircuitBreakerPlugin extends RouteHandlerPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerPlugin.class);

    private CircuitBreaker circuitBreaker;

    public CircuitBreakerPlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
        super(routeOptions, pluginOptions);
    }

    @Override
    public RouteHandler createHandler(Vertx vertx) {
        CircuitBreaker circuitBreaker = createCircuitBreaker(vertx);
        return new CircuitBreakerHandler(routeOptions, circuitBreaker);
    }

    @Override
    public void destory() {
        synchronized (this) {
            if (circuitBreaker != null) {
                circuitBreaker.close();
            }
        }
    }

    private CircuitBreaker createCircuitBreaker(Vertx vertx) {
        synchronized (this) {
            if (circuitBreaker == null) {
                CircuitBreakerOptions cbOptions;
                if (pluginOptions.getOptions() != null) {
                    cbOptions = new CircuitBreakerOptions(pluginOptions.getOptions());
                } else {
                    cbOptions = new CircuitBreakerOptions();
                    // cbOptions.setFailuresRollingWindow(10000);
                    cbOptions.setMaxFailures(10);
                    // If an action is not completed before this timeout, the action is considered as a failure.
                    cbOptions.setTimeout(3000);
                    // does not succeed in time
                    cbOptions.setFallbackOnFailure(false);
                    // time spent in open state before attempting to re-try
                    cbOptions.setResetTimeout(5000);
                }
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
