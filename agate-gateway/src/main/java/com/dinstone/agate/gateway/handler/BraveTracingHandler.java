package com.dinstone.agate.gateway.handler;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;
import com.dinstone.agate.tracing.HttpServerTracing;

import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.ext.web.RoutingContext;

public class BraveTracingHandler implements BeforeHandler {

	private ApiOptions apiOptions;

	private HttpTracing httpTracing;

	public BraveTracingHandler(ApiOptions apiOptions, HttpTracing httpTracing) {
		this.apiOptions = apiOptions;
		this.httpTracing = httpTracing;
	}

	@Override
	public void handle(RoutingContext context) {
		HttpServerTracing tracing = new HttpServerTracing(httpTracing);
		try (Scope scope = tracing.start(context.request()).scope()) {
			tracing.name(apiOptions.getApiName()).tag("app.name", apiOptions.getAppName());
			context.addHeadersEndHandler(v -> {
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
