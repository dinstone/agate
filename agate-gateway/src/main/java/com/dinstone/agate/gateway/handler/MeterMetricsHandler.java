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

import java.util.Arrays;
import java.util.List;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import io.vertx.ext.web.RoutingContext;

public class MeterMetricsHandler implements BeforeHandler {

	private Counter count;

	private Counter error;

	private Timer timer;

	public MeterMetricsHandler(ApiOptions apiOptions, MeterRegistry meterRegistry) {
		List<Tag> tags = Arrays.asList(Tag.of("app.name", apiOptions.getAppName()));
		this.count = meterRegistry.counter(apiOptions.getApiName() + "_count", tags);
		this.error = meterRegistry.counter(apiOptions.getApiName() + "_error", tags);
		this.timer = meterRegistry.timer(apiOptions.getApiName() + "_time", tags);
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
