package io.vertx.tracing.zipkin;

import brave.http.HttpClientResponse;

public class HttpClientResponseWrapper extends HttpClientResponse {

	private io.vertx.core.http.HttpClientResponse delegate;

	public HttpClientResponseWrapper(io.vertx.core.http.HttpClientResponse delegate) {
		this.delegate = delegate;
	}

	@Override
	public int statusCode() {
		return delegate.statusCode();
	}

	@Override
	public Object unwrap() {
		return delegate;
	}

}
