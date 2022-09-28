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
package com.dinstone.agate.gateway.handler.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.handler.FilteringHandler;
import com.dinstone.agate.gateway.options.RouteOptions;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.ext.web.RoutingContext;

public class CircuitBreakerHandler implements FilteringHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerHandler.class);

    private CircuitBreaker circuitBreaker;

    private RouteOptions routeOptions;

    public CircuitBreakerHandler(RouteOptions routeOptions, CircuitBreaker circuitBreaker) {
        this.routeOptions = routeOptions;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public void handle(RoutingContext rc) {
        circuitBreaker.<Void> executeWithFallback(promise -> {
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

}
