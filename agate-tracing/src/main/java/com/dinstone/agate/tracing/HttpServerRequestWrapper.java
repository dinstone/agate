/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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