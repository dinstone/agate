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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class VertxUnitTest {

	private Vertx vertx;

	@Before
	public void before() {
		VertxOptions options = new VertxOptions().setTracingOptions(new ZipkinTracingOptions("agate-test"));
		vertx = Vertx.vertx(options);
	}

	@After
	public void after(TestContext ctx) {
		vertx.close(ctx.asyncAssertSuccess());
	}

	/**
	 * Attention: 9090 port maybe be used by other system service
	 * 
	 * @param tc
	 */
	@Test
	public void testHttpServer(TestContext tc) {
		Async sw = tc.async();
		vertx.createHttpServer().requestHandler(req -> {
			req.response().end("ok");
		}).listen(9191, ar -> {
			System.out.println("listen ok");
			sw.complete();
		});
		sw.awaitSuccess();

		Async cw = tc.async();
		HttpClient client = vertx.createHttpClient();
		client.get(9191, "localhost", "/zipkin/", ar -> {
			if (ar.succeeded()) {
				HttpClientResponse response = ar.result();
				response.body().onComplete(rar -> {
					System.out.println(rar.result().toString());
				});
			}
			cw.complete();
		});
		cw.awaitSuccess();
	}

}
