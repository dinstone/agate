/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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
		return delegate.getMethod().name();
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
