package com.dinstone.agate.tracing;

import brave.Span;
import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 * <pre>
 * HttpClientTracing tracing = new HttpClientTracing(httpTracing);
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
 * } catch (Throwable error) {
 * 	tracing.failure(error);
 * }
 * </pre>
 * 
 * @author dinstone
 *
 */
public class HttpClientTracing {

	private HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> clientHandler;
	private CurrentTraceContext traceContext;
	private Span span;

	public HttpClientTracing(Tracing tracing) {
		this(HttpTracing.create(tracing));
	}

	public HttpClientTracing(HttpTracing httpTracing) {
		this.clientHandler = HttpClientHandler.create(httpTracing);
		this.traceContext = httpTracing.tracing().currentTraceContext();
	}

	public HttpClientTracing start(HttpClientRequest clientRequest) {
		span = clientHandler.handleSend(new HttpClientRequestWrap(clientRequest));
		return this;
	}

	public Scope scope() {
		return traceContext.newScope(span.context());
	}

	public HttpClientTracing failure(Throwable error) {
		if (error == null) {
			error = new RuntimeException("client tracing unkown error");
		}
		return finish(null, error);
	}

	public HttpClientTracing success(HttpClientResponse clientResponse) {
		return finish(clientResponse, null);
	}

	public HttpClientTracing finish(HttpClientResponse clientResponse, Throwable error) {
		clientHandler.handleReceive(new HttpClientResponseWrapper(clientResponse), error, span);
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
