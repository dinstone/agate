package io.vertx.tracing.zipkin;

import brave.http.HttpServerResponse;

public class HttpServerResponseWrapper extends HttpServerResponse {

	private io.vertx.core.http.HttpServerResponse delegate;

	public HttpServerResponseWrapper(io.vertx.core.http.HttpServerResponse delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object unwrap() {
		return delegate;
	}

	@Override
	public int statusCode() {
		return delegate.getStatusCode();
	}

}