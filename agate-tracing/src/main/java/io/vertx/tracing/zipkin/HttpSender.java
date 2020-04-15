package io.vertx.tracing.zipkin;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.codec.Encoding;
import zipkin2.reporter.Sender;

/**
 * An HTTP sender using Vert.x HttpClient, only JSON encoding is supported.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class HttpSender extends Sender {

	private static final CharSequence APPLICATION_JSON = HttpHeaders.createOptimized("application/json");

	private final int messageMaxBytes = 5242880;
	private final Vertx vertx;
	private final String endpoint;
	private final HttpClient client;

	public HttpSender(HttpSenderOptions options) {
		this.endpoint = options.getEndpoint();
		this.vertx = Vertx.vertx(vertxOptions());
		this.client = vertx.createHttpClient(options);
	}

	private VertxOptions vertxOptions() {
		VertxOptions options = new VertxOptions().setEventLoopPoolSize(1);
		return options.setWorkerPoolSize(1).setInternalBlockingPoolSize(1);
	}

	@Override
	public Encoding encoding() {
		return Encoding.JSON;
	}

	@Override
	public int messageMaxBytes() {
		return messageMaxBytes;
	}

	@Override
	public int messageSizeInBytes(List<byte[]> encodedSpans) {
		int val = 2;
		int length = encodedSpans.size();
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				++val;
			}
			val += encodedSpans.get(i).length;
		}
		return val;
	}

	@Override
	public Call<Void> sendSpans(List<byte[]> encodedSpans) {
		int capacity = messageSizeInBytes(encodedSpans);
		Buffer body = Buffer.buffer(capacity);
		body.appendByte((byte) '[');
		for (int i = 0; i < encodedSpans.size(); i++) {
			if (i > 0) {
				body.appendByte((byte) ',');
			}
			body.appendBytes(encodedSpans.get(i));
		}
		body.appendByte((byte) ']');
		return new PostCall(body);
	}

	private class PostCall extends Call<Void> {

		private final Buffer body;

		PostCall(Buffer body) {
			this.body = body;
		}

		@Override
		public Void execute() throws IOException {
			CompletableFuture<Void> future = new CompletableFuture<>();
			asyncSend(new Callback<Void>() {

				@Override
				public void onSuccess(Void value) {
					future.complete(null);
				}

				@Override
				public void onError(Throwable t) {
					future.completeExceptionally(t);
				}

			});
			try {
				return future.get(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new InterruptedIOException();
			} catch (ExecutionException e) {
				throw new IOException(e.getCause());
			} catch (TimeoutException e) {
				throw new IOException(e);
			}
		}

		private void asyncSend(Callback<Void> callback) {
			HttpClientRequest post;
			if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
				post = client.postAbs(endpoint);
			} else {
				post = client.post(endpoint);
			}
			post.putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON).setTimeout(20000).setHandler(ar -> {
				if (ar.succeeded()) {
					callback.onSuccess(null);
				} else {
					callback.onError(ar.cause());
				}
			}).end(body);
		}

		@Override
		public void enqueue(Callback<Void> callback) {
			asyncSend(callback);
		}

		@Override
		public void cancel() {
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public Call<Void> clone() {
			return new PostCall(body);
		}
	}

	@Override
	public void close() throws IOException {
		client.close();
		vertx.close();
	}

	@Override
	public String toString() {
		return "VertxHttpSender{" + endpoint + "}";
	}
}
