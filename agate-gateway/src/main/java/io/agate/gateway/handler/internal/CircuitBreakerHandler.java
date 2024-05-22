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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.handler.OrderedHandler;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class CircuitBreakerHandler extends OrderedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerHandler.class);

    private final RouteOptions routeOptions;

    private CircuitBreaker circuitBreaker;

    public CircuitBreakerHandler(Vertx vertx, RouteOptions routeOptions, PluginOptions pluginOptions) {
        super(400);

        this.routeOptions = routeOptions;

        createCircuitBreaker(vertx, routeOptions, pluginOptions);
    }

    private void createCircuitBreaker(Vertx vertx, RouteOptions routeOptions, PluginOptions pluginOptions) {
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
        circuitBreaker = CircuitBreaker.create(routeOptions.getRoute(), vertx, cbOptions)
                .openHandler(v -> {
                    LOG.debug("circuit breaker {} open", circuitBreaker.name());
                }).closeHandler(v -> {
                    LOG.debug("circuit breaker {} close", circuitBreaker.name());
                }).halfOpenHandler(v -> {
                    LOG.debug("circuit breaker {} half", circuitBreaker.name());
                });
    }

    @Override
    public void handle(RoutingContext rc) {
        circuitBreaker.<Void>executeWithFallback(promise -> {
            rc.addEndHandler(ar -> {
                if (rc.response().getStatusCode() >= 400) {
                    LOG.warn("route={} status={}", routeOptions.getRoute(), rc.response().getStatusCode());
                    promise.fail("status = " + rc.statusCode());
                } else {
                    promise.complete();
                }
            });
            rc.next();
        }, t -> {
            // gateway time-out
            rc.fail(504, t);
            return null;
        });
    }

    @Override
    public void destroy() {
        if (circuitBreaker != null) {
            circuitBreaker.close();
        }
    }
}
