package io.vertx.tracing.zipkin;

import java.util.Map;
import java.util.function.BiConsumer;

import brave.Span;
import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpServerHandler;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.TraceContext;
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
	private final boolean closeTracer;
	private final HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> serverHandler;
	private final HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler;

	public ZipkinTracer(boolean closeTracer, Tracing tracing) {
		this(closeTracer, HttpTracing.newBuilder(tracing).build());
	}

	public ZipkinTracer(boolean closeTracer, HttpTracing httpTracing) {
		this.closeTracer = closeTracer;
		this.tracing = httpTracing.tracing();
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
			TraceContext parent = context.getLocal(ACTIVE_CONTEXT);
			Span span;
//			if (parent != null) {
//				span = clientHandler.handleSendWithParent(new HttpClientRequestWrap(clientRequest, headers), parent);
//			} else {
//			}
			span = clientHandler.handleSend(new HttpClientRequestWrap(clientRequest, headers));
			SocketAddress socketAddress = clientRequest.connection().remoteAddress();
			span.remoteIpAndPort(socketAddress.host(), socketAddress.port());

			return (resp, err) -> {
				clientHandler.handleReceive(new HttpClientResponseWrapper((HttpClientResponse) resp), err, span);
			};
		}
		return null;
	}

	@Override
	public <R> void receiveResponse(Context context, R response, BiConsumer<Object, Throwable> payload,
			Throwable failure, TagExtractor<R> tagExtractor) {
		if (payload != null && response instanceof HttpClientResponse) {
			payload.accept(response, failure);
		}
	}

	@Override
	public void close() {
		if (closeTracer) {
			tracing.close();
		}
	}
}
