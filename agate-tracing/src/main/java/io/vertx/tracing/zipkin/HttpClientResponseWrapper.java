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
