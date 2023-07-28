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
package io.agate.gateway.handler.internal;

import java.util.Arrays;
import java.util.List;

import io.agate.gateway.handler.OrderedHandler;
import io.agate.gateway.options.RouteOptions;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import io.vertx.ext.web.RoutingContext;

public class MeterMetricsHandler extends OrderedHandler {

    private Counter count;

    private Counter error;

    private Timer timer;

    public MeterMetricsHandler(RouteOptions routeOptions, MeterRegistry meterRegistry) {
    	super(100);
    	
        List<Tag> tags = Arrays.asList(Tag.of("gateway", routeOptions.getGateway()));
        this.count = meterRegistry.counter(routeOptions.getRoute() + "_count", tags);
        this.error = meterRegistry.counter(routeOptions.getRoute() + "_error", tags);
        this.timer = meterRegistry.timer(routeOptions.getRoute() + "_time", tags);
    }

    @Override
    public void handle(RoutingContext context) {
        Sample sample = Timer.start();
        context.addBodyEndHandler(v -> {
            sample.stop(timer);

            count.increment();
            if (isErrorCode(context.response().getStatusCode())) {
                error.increment();
            }
        });
        context.next();
    }

    private boolean isErrorCode(int statusCode) {
        return statusCode < 200 || statusCode >= 400;
    }

}
