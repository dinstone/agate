package com.dinstone.agate.tracing;

import brave.Span;
import brave.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;

public class HttpServerRequestWrapper extends HttpServerRequest {

	private final io.vertx.core.http.HttpServerRequest delegate;

	public HttpServerRequestWrapper(io.vertx.core.http.HttpServerRequest delegate) {
		this.delegate = delegate;
	}

	@Override
	public io.vertx.core.http.HttpServerRequest unwrap() {
		return delegate;
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
	public String route() {
		return delegate.path();
	}

	@Override
	public String header(String name) {
		return delegate.headers().get(name);
	}

	@Override
	public boolean parseClientIpAndPort(Span span) {
		if (parseClientIpFromXForwardedFor(span)) {
			return true;
		}
		SocketAddress addr = delegate.remoteAddress();
		return span.remoteIpAndPort(addr.host(), addr.port());
	}
}