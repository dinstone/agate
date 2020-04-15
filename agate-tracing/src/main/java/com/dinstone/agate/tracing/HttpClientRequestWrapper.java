package com.dinstone.agate.tracing;

import brave.http.HttpClientRequest;

public class HttpClientRequestWrapper extends HttpClientRequest {

	private io.vertx.core.http.HttpClientRequest delegate;

	public HttpClientRequestWrapper(io.vertx.core.http.HttpClientRequest delegate) {
		this.delegate = delegate;
	}

	@Override
	public void header(String name, String value) {
		delegate.putHeader(name, value);
	}

	@Override
	public String method() {
		return delegate.method().name();
	}

	@Override
	public String path() {
		return delegate.path();
	}

	@Override
	public String url() {
		return delegate.absoluteURI();
	}

	@Override
	public String header(String name) {
		return delegate.headers().get(name);
	}

	@Override
	public Object unwrap() {
		return delegate;
	}

	@Override
	public String route() {
		return delegate.path();
	}

}
