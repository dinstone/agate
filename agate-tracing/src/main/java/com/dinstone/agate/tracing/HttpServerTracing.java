package com.dinstone.agate.tracing;

import brave.Span;
import brave.Tracing;
import brave.http.HttpServerHandler;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * <pre>
 * {@code}
 * public void handle(RoutingContext context) {
 *	HttpServerTracing tracing = new HttpServerTracing(httpTracing);
 *	try (Scope scope = tracing.start(context.request()).scope()) {
 *		tracing.tag("app.name", apiOptions.getAppName()).tag("api.name", apiOptions.getApiName());
 *		context.addHeadersEndHandler(v -> {
 *			if (context.failed()) {
 *				tracing.failure(context.failure());
 *			} else {
 *				tracing.success(context.response());
 *			}
 *		});
 *
 *		context.next();
 *	}
 *}
 * </pre>
 * 
 * @author dinstone
 *
 */
public class HttpServerTracing {

	private HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private CurrentTraceContext traceContext;
	private Span span;

	public HttpServerTracing(Tracing tracing) {
		this(HttpTracing.create(tracing));
	}

	public HttpServerTracing(HttpTracing httpTracing) {
		serverHandler = HttpServerHandler.create(httpTracing);
		traceContext = httpTracing.tracing().currentTraceContext();
	}

	public HttpServerTracing start(HttpServerRequest serverRequest) {
		this.span = serverHandler.handleReceive(new HttpServerRequestWrapper(serverRequest));
		return this;
	}

	public Scope scope() {
		return traceContext.maybeScope(span.context());
	}

	public HttpServerTracing success(HttpServerResponse serverResponse) {
		return finish(serverResponse, null);
	}

	public HttpServerTracing failure(Throwable error) {
		if (error == null) {
			error = new RuntimeException("server tracing unkown error");
		}
		return finish(null, error);
	}

	public HttpServerTracing finish(HttpServerResponse serverResponse, Throwable error) {
		serverHandler.handleSend(new HttpServerResponseWrapper(serverResponse), error, span);
		return this;
	}

	/**
	 * add span's tag, must be end of the tracing start.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public HttpServerTracing tag(String key, String value) {
		span.tag(key, value);
		return this;
	}

}
