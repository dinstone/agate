package com.dinstone.agate.tracing;

import brave.http.HttpClientRequest;
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

	@Override
	public HttpClientRequest request() {
		return new HttpClientRequestWrapper(delegate.request());
	}

}
