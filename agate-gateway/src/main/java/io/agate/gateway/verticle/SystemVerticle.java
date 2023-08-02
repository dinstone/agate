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
package io.agate.gateway.verticle;

import io.agate.gateway.context.AddressConstant;
import io.agate.gateway.context.ApplicationContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.micrometer.MetricsService;

/**
 * collect system runtime parameters.
 * 
 * @author dinstone
 *
 */
public class SystemVerticle extends AbstractVerticle {

	private MetricsService metricsService;

	public SystemVerticle(ApplicationContext context) {
	}

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);

		metricsService = MetricsService.create(vertx);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.eventBus().consumer(AddressConstant.APM_METRICS, message -> {
			if (message.body() instanceof String) {
				String baseName = (String) message.body();
				message.reply(metricsService.getMetricsSnapshot(baseName));
			} else {
				message.reply(metricsService.getMetricsSnapshot());
			}
		});

		startPromise.complete();
	}

}
