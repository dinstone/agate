package com.dinstone.agate.tracing;

import brave.Span;
import brave.http.HttpServerHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * <pre>
 * {@code}
 * public void handle(RoutingContext context) {
 *	httpServerTracing tracing = zipkinTracer.httpServerTracing();
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

	private final HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private final CurrentTraceContext traceContext;
	private Span span;

	HttpServerTracing(HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler,
			CurrentTraceContext traceContext) {
		this.serverHandler = serverHandler;
		this.traceContext = traceContext;
	}

	public HttpServerTracing start(HttpServerRequest serverRequest) {
		this.span = serverHandler.handleReceive(new HttpServerRequestWrapper(serverRequest));
		return this;
	}

	public Scope scope() {
		return traceContext.maybeScope(span.context());
	}

	public HttpServerTracing success(HttpServerResponse serverResponse) {
		if (serverResponse == null) {
			throw new IllegalArgumentException("server tracing response is null");
		}
		return finish(serverResponse, null);
	}

	public HttpServerTracing failure(Throwable error) {
		if (error == null) {
			throw new IllegalArgumentException("server tracing error is null");
		}
		return finish(null, error);
	}

	public HttpServerTracing finish(HttpServerResponse serverResponse, Throwable error) {
		HttpServerResponseWrapper response = null;
		if (serverResponse != null) {
			response = new HttpServerResponseWrapper(serverResponse);
		}
		serverHandler.handleSend(response, error, span);
		return this;
	}

	/**
	 * add span's name, must be end of the tracing start.
	 * 
	 * @param name
	 * @return
	 */
	public HttpServerTracing name(String name) {
		span.name(name);
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
