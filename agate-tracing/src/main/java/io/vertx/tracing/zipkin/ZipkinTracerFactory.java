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
package io.vertx.tracing.zipkin;

import java.io.IOException;

import brave.Tracing;
import brave.sampler.Sampler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VertxTracerFactory;
import io.vertx.core.spi.tracing.VertxTracer;
import io.vertx.core.tracing.TracingOptions;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

public class ZipkinTracerFactory implements VertxTracerFactory {

	@Override
	public VertxTracer<?, ?> tracer(TracingOptions options) {
		if (options instanceof ZipkinTracingOptions) {
			return buildVertxTracer((ZipkinTracingOptions) options);
		} else {
			return buildVertxTracer(newOptions());
		}
	}

	private VertxTracer<?, ?> buildVertxTracer(ZipkinTracingOptions zipkinOptions) {
		if (zipkinOptions.getTracing() != null) {
			return new ZipkinTracer(false, zipkinOptions.getTracing());
		} else if (zipkinOptions.getSenderOptions() != null) {
			Sender sender = new HttpSender(zipkinOptions.getSenderOptions());
			Tracing tracing = Tracing.newBuilder().localServiceName(zipkinOptions.getServiceName())
					.spanReporter(AsyncReporter.builder(sender).build())
					.sampler(Sampler.create(zipkinOptions.getProbability())).build();
			return new ZipkinTracer(true, tracing) {
				@Override
				public void close() {
					super.close();
					try {
						sender.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		} else {
			return null;
		}
	}

	@Override
	public ZipkinTracingOptions newOptions() {
		return new ZipkinTracingOptions("vertx-service");
	}

	@Override
	public TracingOptions newOptions(JsonObject jsonObject) {
		return new ZipkinTracingOptions(jsonObject);
	}
}
