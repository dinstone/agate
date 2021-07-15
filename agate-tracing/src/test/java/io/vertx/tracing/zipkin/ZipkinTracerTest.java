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
package io.vertx.tracing.zipkin;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import zipkin2.Span;
import zipkin2.junit.ZipkinRule;

@RunWith(VertxUnitRunner.class)
public class ZipkinTracerTest {

	@Rule
	public ZipkinRule zipkin = new ZipkinRule();

	private Vertx vertx;
	private HttpClient client;

	@Before
	public void before() {
		String url = zipkin.httpUrl() + "/api/v2/spans";
		vertx = Vertx.vertx(new VertxOptions().setTracingOptions(
				new ZipkinTracingOptions("agate-test").setSenderOptions(new HttpSenderOptions().setEndpoint(url))));
		client = vertx.createHttpClient();
	}

	@After
	public void after(TestContext ctx) {
		client.close();
		vertx.close(ctx.asyncAssertSuccess());
	}

	List<Span> waitUntilTrace(int min) throws Exception {
		return waitUntilTrace(zipkin, min);
	}

	static List<Span> waitUntilTrace(ZipkinRule zipkin, int min) throws Exception {
		long now = System.currentTimeMillis();
		while ((System.currentTimeMillis() - now) < 10000) {
			List<List<Span>> traces = zipkin.getTraces();
			if (traces.size() > 0 && traces.get(0).size() >= min) {
				return traces.get(0);
			}
			Thread.sleep(10);
		}
		throw new AssertionError();
	}

	@Test
	public void testHttpServerRequest(TestContext ctx) throws Exception {
		Async listenLatch = ctx.async();
		vertx.createHttpServer().requestHandler(req -> {
			req.response().end();
		}).listen(8080, ctx.asyncAssertSuccess(v -> listenLatch.complete()));
		listenLatch.awaitSuccess();

		Async responseLatch = ctx.async();
		client.request(HttpMethod.GET, 8080, "127.0.0.1", "/url", ar -> {
			responseLatch.complete();
		});
		responseLatch.awaitSuccess();

		List<Span> trace = waitUntilTrace(zipkin, 2);
		assertEquals(2, trace.size());
	}

	@Test
	public void testHttpClientRequest(TestContext ctx) throws Exception {
		Async listenLatch = ctx.async(2);
		vertx.createHttpServer().requestHandler(req -> {
			HttpClient c = vertx.createHttpClient();
			c.request(HttpMethod.GET, 8081, "localhost", "/s2s", ar -> {
				System.out.println("testHttpClientRequest is " + ar.succeeded());
				req.response().end();
			});
		}).listen(8080, ar -> {
			ctx.assertTrue(ar.succeeded(), "Could not bind on port 8080");
			listenLatch.countDown();
		});

		vertx.createHttpServer().requestHandler(req -> {
			req.response().end();
		}).listen(8081, ar -> {
			ctx.assertTrue(ar.succeeded(), "Could not bind on port 8081");
			listenLatch.countDown();
		});
		listenLatch.awaitSuccess();

		Async responseLatch = ctx.async();
		client.request(HttpMethod.GET, 8080, "localhost", "/c2s", ar -> {
			responseLatch.complete();
		});
		responseLatch.awaitSuccess();

		List<Span> trace = waitUntilTrace(4);
		assertEquals(4, trace.size());
		Span span1 = trace.get(0);
		assertEquals("get /s2s", span1.name());
		assertEquals("GET", span1.tags().get("http.method"));
		assertEquals("/s2s", span1.tags().get("http.path"));
		Span span2 = trace.get(1);
		assertEquals("get /c2s", span2.name());
		assertEquals("GET", span2.tags().get("http.method"));
		assertEquals("/c2s", span2.tags().get("http.path"));
	}
}
