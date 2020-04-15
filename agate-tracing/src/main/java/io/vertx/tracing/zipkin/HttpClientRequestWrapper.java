package io.vertx.tracing.zipkin;

import java.util.function.BiConsumer;

import brave.http.HttpClientRequest;

public class HttpClientRequestWrapper extends HttpClientRequest {

	private io.vertx.core.http.HttpClientRequest delegate;
	private BiConsumer<String, String> headers;

	public HttpClientRequestWrapper(io.vertx.core.http.HttpClientRequest delegate, BiConsumer<String, String> headers) {
		this.delegate = delegate;
		this.headers = headers;
	}

	@Override
	public void header(String name, String value) {
		headers.accept(name, value);
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
