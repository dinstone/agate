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
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.RequestOptions;
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
	private final HttpClient client;
	private final String endpoint;
	private Vertx vertx;

	public HttpSender(HttpSenderOptions options) {
		this(options, null);
	}

	public HttpSender(HttpSenderOptions options, Vertx vertx) {
		if (vertx == null) {
			vertx = Vertx.vertx(vertxOptions());
			this.vertx = vertx;
		}
		this.client = vertx.createHttpClient(options);
		this.endpoint = options.getEndpoint();
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
			RequestOptions options = new RequestOptions().setAbsoluteURI(endpoint)
					.addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON).setTimeout(20000);
			client.request(options).onSuccess(request -> {
				request.end(body);
				callback.onSuccess(null);
			}).onFailure(t -> {
				callback.onError(t);
			});
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
		if (vertx != null) {
			vertx.close();
		}
	}

	@Override
	public String toString() {
		return "VertxHttpSender{" + endpoint + "}";
	}
}
