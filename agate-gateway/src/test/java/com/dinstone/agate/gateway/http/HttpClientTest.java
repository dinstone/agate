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
package com.dinstone.agate.gateway.http;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;

public class HttpClientTest {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(999999999));

		HttpClientOptions options = new HttpClientOptions().setMaxPoolSize(1).setMaxWaitQueueSize(2);
		HttpClient client = vertx.createHttpClient(options);

		new Thread() {
			public void run() {
				client.request(HttpMethod.GET, "http://localhost:8081/hr/a").onSuccess(request -> {
					request.send().onSuccess(response -> {
						System.out.println(response.getHeader(HttpHeaders.CONTENT_LENGTH));
					});
				}).onFailure(t -> {
					System.out.println(Thread.currentThread().getName() + ": error is " + t);
				});

				System.out.println("thread is over");
			}

		}.start();

		System.out.println("main is over");
	}

}
