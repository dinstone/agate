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

import io.agate.gateway.handler.OrderedHandler;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.internal.RedisRateLimiter;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rate limit handler.
 *
 * @author dinstone
 */
public class RateLimitHandler extends OrderedHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RateLimitHandler.class);

    private RouteOptions routeOptions;

    private RedisRateLimiter rateLimiter;

    public RateLimitHandler(RouteOptions routeOptions, RedisRateLimiter rateLimiter) {
        super(400);

        this.routeOptions = routeOptions;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void handle(RoutingContext rc) {
        rateLimiter.acquire().onSuccess(b -> {
            if (b) {
                rc.next();
            } else {
                rc.fail(new HttpException(429, "too many requests, rate limit " + rateLimiter.getCount() + "/" + rateLimiter.getPeriod()));
            }
        }).onFailure(e -> {
            rc.fail(new HttpException(429, "rate limit error: " + e.getMessage()));
        });
    }

}
