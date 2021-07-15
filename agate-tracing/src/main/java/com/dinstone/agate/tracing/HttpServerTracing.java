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
package com.dinstone.agate.tracing;

import brave.Span;
import brave.http.HttpServerHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * <pre>
 * {@code}
 * public void handle(RoutingContext context) {
 *	httpServerTracing tracing = zipkinTracer.httpServerTracing();
 *	try (Scope scope = tracing.start(context.request()).scope()) {
 *		tracing.tag("app.name", apiOptions.getAppName()).tag("api.name", apiOptions.getApiName());
 *		context.addHeadersEndHandler(v -> {
 *			if (context.failed()) {
 *				tracing.failure(context.failure());
 *			} else {
 *				tracing.success(context.response());
 *			}
 *		});
 *
 *		context.next();
 *	}
 *}
 * </pre>
 * 
 * @author dinstone
 *
 */
public class HttpServerTracing {

	private final HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private final CurrentTraceContext traceContext;
	private Span span;

	HttpServerTracing(HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler,
			CurrentTraceContext traceContext) {
		this.serverHandler = serverHandler;
		this.traceContext = traceContext;
	}

	public HttpServerTracing start(HttpServerRequest serverRequest) {
		this.span = serverHandler.handleReceive(new HttpServerRequestWrapper(serverRequest));
		return this;
	}

	public Scope scope() {
		return traceContext.maybeScope(span.context());
	}

	public HttpServerTracing success(HttpServerResponse serverResponse) {
		if (serverResponse == null) {
			throw new IllegalArgumentException("server tracing response is null");
		}
		return finish(serverResponse, null);
	}

	public HttpServerTracing failure(Throwable error) {
		if (error == null) {
			throw new IllegalArgumentException("server tracing error is null");
		}
		return finish(null, error);
	}

	public HttpServerTracing finish(HttpServerResponse serverResponse, Throwable error) {
		HttpServerResponseWrapper response = null;
		if (serverResponse != null) {
			response = new HttpServerResponseWrapper(serverResponse);
		}
		serverHandler.handleSend(response, error, span);
		return this;
	}

	/**
	 * add span's name, must be end of the tracing start.
	 * 
	 * @param name
	 * @return
	 */
	public HttpServerTracing name(String name) {
		span.name(name);
		return this;
	}

	/**
	 * add span's tag, must be end of the tracing start.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public HttpServerTracing tag(String key, String value) {
		span.tag(key, value);
		return this;
	}

}
