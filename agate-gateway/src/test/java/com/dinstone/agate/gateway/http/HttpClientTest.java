package com.dinstone.agate.gateway.http;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;

public class HttpClientTest {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(999999999));

		HttpClientOptions options = new HttpClientOptions().setMaxPoolSize(1).setMaxWaitQueueSize(2);
		HttpClient client = vertx.createHttpClient(options);

		for (int i = 0; i < 1; i++) {
			new Thread() {
				public void run() {
					client.getAbs("http://localhost:8081/hr/a", ar -> {
						if (ar.succeeded()) {
							HttpClientResponse response = ar.result();
							System.out.println(response.getHeader(HttpHeaders.CONTENT_LENGTH));
						} else {
							System.out.println(Thread.currentThread().getName() + ": error is " + ar.cause());
						}
					}).end();
				};
			}.start();
		}

	}

}
