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
import brave.http.HttpClientHandler;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.net.SocketAddress;

/**
 * <pre>
 * httpClientTracing tracing = zipkinTracer.httpClientTracing();
 * try (Scope scope = tracing.start(beRequest).scope()) {
 * 	// exception handler
 * 	beRequest.exceptionHandler(error -> {
 * 		tracing.failure(error);
 * 		if (error instanceof ConnectException || error instanceof TimeoutException) {
 * 			rc.fail(503, new RuntimeException("backend service unavailable", error));
 * 		} else {
 * 			rc.fail(500, new RuntimeException("request backend service error", error));
 * 		}
 * 	});
 * 	// response handler
 * 	beRequest.setHandler(ar -> {
 * 		if (ar.succeeded()) {
 * 			tracing.success(ar.result());
 * 			rc.put("backend.response", ar.result()).next();
 * 		} else {
 * 			tracing.failure(ar.cause());
 * 			rc.fail(503, new RuntimeException("backend response is error", ar.cause()));
 * 		}
 * 	});
 * </pre>
 * 
 * @author dinstone
 *
 */
public class HttpClientTracing {

	private final HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler;
	private final CurrentTraceContext traceContext;
	private Span span;

	HttpClientTracing(HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler,
			CurrentTraceContext traceContext) {
		super();
		this.clientHandler = clientHandler;
		this.traceContext = traceContext;
	}

	public HttpClientTracing start(HttpClientRequest clientRequest) {
		span = clientHandler.handleSend(new HttpClientRequestWrapper(clientRequest));
		return this;
	}

	public Scope scope() {
		return traceContext.newScope(span.context());
	}

	public HttpClientTracing failure(Throwable error) {
		if (error == null) {
			throw new IllegalArgumentException("client tracing error is null");
		}
		return finish(null, error);
	}

	public HttpClientTracing success(HttpClientResponse clientResponse) {
		if (clientResponse == null) {
			throw new IllegalArgumentException("client tracing response is null");
		}
		return finish(clientResponse, null);
	}

	public HttpClientTracing finish(HttpClientResponse clientResponse, Throwable error) {
		HttpClientResponseWrapper response = null;
		if (clientResponse != null) {
			response = new HttpClientResponseWrapper(clientResponse);
			try {
				SocketAddress socketAddress = clientResponse.request().connection().remoteAddress();
				span.remoteIpAndPort(socketAddress.host(), socketAddress.port());
			} catch (Exception e) {
				// ignore
			}
		}
		clientHandler.handleReceive(response, error, span);
		return this;
	}

	/**
	 * add span's name, must be end of the tracing start.
	 * 
	 * @param name
	 * @return
	 */
	public HttpClientTracing name(String name) {
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
	public HttpClientTracing tag(String key, String value) {
		span.tag(key, value);
		return this;
	}
}
