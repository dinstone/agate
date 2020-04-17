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
