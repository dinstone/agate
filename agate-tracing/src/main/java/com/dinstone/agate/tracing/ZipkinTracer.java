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

import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpServerHandler;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import io.vertx.tracing.zipkin.HttpRequestResponseParser;

public class ZipkinTracer {
	private HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler;
	private CurrentTraceContext traceContext;

	public ZipkinTracer(Tracing tracing) {
		HttpRequestResponseParser parser = new HttpRequestResponseParser();
		HttpTracing httpTracing = HttpTracing.newBuilder(tracing).clientRequestParser(parser)
				.clientResponseParser(parser).serverRequestParser(parser).serverResponseParser(parser).build();
		this.clientHandler = HttpClientHandler.create(httpTracing);
		this.serverHandler = HttpServerHandler.create(httpTracing);
		this.traceContext = tracing.currentTraceContext();
	}

	public HttpClientTracing httpClientTracing() {
		return new HttpClientTracing(clientHandler, traceContext);
	}

	public HttpServerTracing httpServerTracing() {
		return new HttpServerTracing(serverHandler, traceContext);
	}

	public void destroy() {
	}

}
