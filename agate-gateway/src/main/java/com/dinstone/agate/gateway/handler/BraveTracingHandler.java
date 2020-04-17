package com.dinstone.agate.gateway.handler;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;
import com.dinstone.agate.tracing.HttpServerTracing;
import com.dinstone.agate.tracing.ZipkinTracer;

import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.ext.web.RoutingContext;

public class BraveTracingHandler implements BeforeHandler {

	private ApiOptions apiOptions;

	private ZipkinTracer zipkinTracer;

	public BraveTracingHandler(ApiOptions apiOptions, ZipkinTracer zipkinTracer) {
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
			tracing.tag("api.name", apiOptions.getApiName()).tag("app.name", apiOptions.getAppName());
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
