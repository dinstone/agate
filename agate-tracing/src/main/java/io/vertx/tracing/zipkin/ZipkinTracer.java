/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import java.util.Map;
import java.util.function.BiConsumer;

import brave.Span;
import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpServerHandler;
import brave.http.HttpTracing;
import io.vertx.core.Context;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.tracing.TagExtractor;

/**
 * - https://zipkin.io/pages/instrumenting.html <br>
 * - https://zipkin.io/public/thrift/v1/zipkinCore.html
 */
public class ZipkinTracer implements io.vertx.core.spi.tracing.VertxTracer<Span, BiConsumer<Object, Throwable>> {
	// docker run --rm -ti -p 9411:9411 openzipkin/zipkin
	private static final String ACTIVE_SPAN = "vertx.tracing.zipkin.active_span";
	private static final String ACTIVE_CONTEXT = "vertx.tracing.zipkin.active_context";

	private final Tracing tracing;
	private final boolean closeit;
	private final HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private final HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler;

	public ZipkinTracer(boolean closeit, Tracing tracing) {
		this.closeit = closeit;
		this.tracing = tracing;

		HttpRequestResponseParser parser = new HttpRequestResponseParser();
		HttpTracing httpTracing = HttpTracing.newBuilder(tracing).clientRequestParser(parser)
				.clientResponseParser(parser).serverRequestParser(parser).serverResponseParser(parser).build();
		this.clientHandler = HttpClientHandler.create(httpTracing);
		this.serverHandler = HttpServerHandler.create(httpTracing);
	}

	@Override
	public <R> Span receiveRequest(Context context, R request, String operation,
			Iterable<Map.Entry<String, String>> headers, TagExtractor<R> tagExtractor) {
		if (request instanceof HttpServerRequest) {
			HttpServerRequest serverRequest = (HttpServerRequest) request;
			Span span = serverHandler.handleReceive(new HttpServerRequestWrapper(serverRequest));
			if (span != null) {
				context.putLocal(ACTIVE_SPAN, span);
				context.putLocal(ACTIVE_CONTEXT, span.context());
			}
			return span;
		}
		return null;
	}

	@Override
	public <R> void sendResponse(Context context, R response, Span span, Throwable failure,
			TagExtractor<R> tagExtractor) {
		if (span != null) {
			if (response instanceof HttpServerResponse) {
				context.removeLocal(ACTIVE_SPAN);
				context.removeLocal(ACTIVE_CONTEXT);
				HttpServerResponseWrapper serverResponse = new HttpServerResponseWrapper((HttpServerResponse) response);
				serverHandler.handleSend(serverResponse, failure, span);
			}
		}
	}

	@Override
	public <R> BiConsumer<Object, Throwable> sendRequest(Context context, R request, String operation,
			BiConsumer<String, String> headers, TagExtractor<R> tagExtractor) {
		if (request instanceof HttpClientRequest) {
			HttpClientRequest clientRequest = (HttpClientRequest) request;
			HttpClientRequestWrapper requestWrap = new HttpClientRequestWrapper(clientRequest, headers);
			Span span;
			if (context.getLocal(ACTIVE_CONTEXT) != null) {
				span = clientHandler.handleSendWithParent(requestWrap, context.getLocal(ACTIVE_CONTEXT));
			} else {
				span = clientHandler.handleSend(requestWrap);
			}
			SocketAddress socketAddress = clientRequest.connection().remoteAddress();
			span.remoteIpAndPort(socketAddress.host(), socketAddress.port());

			return (resp, err) -> {
				HttpClientResponseWrapper response = null;
				if (resp != null) {
					response = new HttpClientResponseWrapper((HttpClientResponse) resp);
				}
				clientHandler.handleReceive(response, err, span);
			};
		}
		return null;
	}

	@Override
	public <R> void receiveResponse(Context context, R response, BiConsumer<Object, Throwable> payload,
			Throwable failure, TagExtractor<R> tagExtractor) {
		if (payload != null) {
			payload.accept(response, failure);
		}
	}

	@Override
	public void close() {
		if (closeit) {
			tracing.close();
		}
	}
}
