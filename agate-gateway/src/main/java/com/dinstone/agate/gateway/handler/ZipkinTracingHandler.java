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

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;
import com.dinstone.agate.tracing.HttpServerTracing;
import com.dinstone.agate.tracing.ZipkinTracer;

import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.ext.web.RoutingContext;

public class ZipkinTracingHandler implements BeforeHandler {

	private ApiOptions apiOptions;

	private ZipkinTracer zipkinTracer;

	public ZipkinTracingHandler(ApiOptions apiOptions, ZipkinTracer zipkinTracer) {
		this.apiOptions = apiOptions;
		this.zipkinTracer = zipkinTracer;
	}

	@Override
	public void handle(RoutingContext context) {
		if (zipkinTracer == null) {
			context.next();
		}

		HttpServerTracing tracing = zipkinTracer.httpServerTracing();
		try (Scope scope = tracing.start(context.request()).scope()) {
			tracing.tag("api.name", apiOptions.getApiName()).tag("app.name", apiOptions.getGateway());
			context.addBodyEndHandler(v -> {
				if (context.failed()) {
					tracing.failure(context.failure());
				} else {
					tracing.success(context.response());
				}
			});

			context.next();
		}
	}

}
