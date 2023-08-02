/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.gateway;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.netty.handler.codec.DecoderResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpFrame;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerFileUpload;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.StreamPriority;
import io.vertx.core.http.impl.HttpServerRequestInternal;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class RouteTest {

    public static class HttpServerResponseMock implements HttpServerResponse {

        @Override
        public Future<Void> write(Buffer data) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void write(Buffer data, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public void end(Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean writeQueueFull() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public HttpServerResponse exceptionHandler(Handler<Throwable> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse setWriteQueueMaxSize(int maxSize) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse drainHandler(Handler<Void> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getStatusCode() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public HttpServerResponse setStatusCode(int statusCode) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getStatusMessage() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse setStatusMessage(String statusMessage) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse setChunked(boolean chunked) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isChunked() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public MultiMap headers() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putHeader(String name, String value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putHeader(CharSequence name, CharSequence value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putHeader(String name, Iterable<String> values) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putHeader(CharSequence name, Iterable<CharSequence> values) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultiMap trailers() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putTrailer(String name, String value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putTrailer(CharSequence name, CharSequence value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putTrailer(String name, Iterable<String> values) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse putTrailer(CharSequence name, Iterable<CharSequence> value) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse closeHandler(Handler<Void> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse endHandler(Handler<Void> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Future<Void> write(String chunk, String enc) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void write(String chunk, String enc, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public Future<Void> write(String chunk) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void write(String chunk, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public HttpServerResponse writeContinue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Future<Void> end(String chunk) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void end(String chunk, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public Future<Void> end(String chunk, String enc) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void end(String chunk, String enc, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public Future<Void> end(Buffer chunk) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void end(Buffer chunk, Handler<AsyncResult<Void>> handler) {
            // TODO Auto-generated method stub

        }

        @Override
        public Future<Void> end() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Future<Void> sendFile(String filename, long offset, long length) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse sendFile(String filename, long offset, long length,
                Handler<AsyncResult<Void>> resultHandler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void close() {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean ended() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean closed() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean headWritten() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public HttpServerResponse headersEndHandler(Handler<Void> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse bodyEndHandler(Handler<Void> handler) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long bytesWritten() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int streamId() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Future<HttpServerResponse> push(HttpMethod method, String host, String path, MultiMap headers) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean reset(long code) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public HttpServerResponse writeCustomFrame(int type, int flags, Buffer payload) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerResponse addCookie(Cookie cookie) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Cookie removeCookie(String name, boolean invalidate) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Cookie> removeCookies(String name, boolean invalidate) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Cookie removeCookie(String name, String domain, String path, boolean invalidate) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private static final class HttpServerRequestMock extends HttpServerRequestInternal {

        private String path;

        private HttpMethod method;

        public HttpServerRequestMock(HttpMethod method, String path) {
            this.path = path;
            this.method = method;
        }

        @Override
        public HttpVersion version() {
            return HttpVersion.HTTP_1_1;
        }

        @Override
        public String uri() {
            return path;
        }

        @Override
        public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> uploadHandler) {
            return this;
        }

        @Override
        public Future<ServerWebSocket> toWebSocket() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Future<NetSocket> toNetSocket() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest streamPriorityHandler(Handler<StreamPriority> handler) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public HttpServerRequest setExpectMultipart(boolean expect) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public String scheme() {
            // TODO Auto-generated method stub
            return "http";
        }

        @Override
        public HttpServerRequest resume() {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public HttpServerResponse response() {
            // TODO Auto-generated method stub
            return new HttpServerResponseMock();
        }

        @Override
        public String query() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest pause() {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public String path() {
            // TODO Auto-generated method stub
            return path;
        }

        @Override
        public MultiMap params() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpMethod method() {
            return method;
        }

        @Override
        public boolean isExpectMultipart() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isEnded() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public String host() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultiMap headers() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest handler(Handler<Buffer> handler) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public String getFormAttribute(String attributeName) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Cookie getCookie(String name, String domain, String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Cookie getCookie(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MultiMap formAttributes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest fetch(long amount) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public HttpServerRequest endHandler(Handler<Void> endHandler) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public Future<Void> end() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DecoderResult decoderResult() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest customFrameHandler(Handler<HttpFrame> handler) {
            // TODO Auto-generated method stub
            return this;
        }

        @Override
        public Set<Cookie> cookies(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<Cookie> cookies() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpConnection connection() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long bytesRead() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Future<Buffer> body() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String absoluteURI() {
            // TODO Auto-generated method stub
            return path;
        }

        @Override
        public Context context() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object metric() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpServerRequest setParamsCharset(String charset) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getParamsCharset() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    void httpserverTest(Vertx vertx, VertxTestContext testContext) throws Throwable {
        vertx.createHttpServer().requestHandler(req -> req.response().end("ok")).listen(16969)
                .onComplete(testContext.succeedingThenComplete());

        testContext.awaitCompletion(5, TimeUnit.SECONDS);

        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }

    @Test
    void routeTest(Vertx vertx, VertxTestContext testContext) throws Throwable {
        Handler<RoutingContext> handler = rx -> {
            // rx.response().end(rx.request().path());
            testContext.completeNow();
        };

        Router router = Router.router(vertx);
        for (int i = 0; i < 10000; i++) {
            router.get("/get/index/" + i).handler(handler);
        }
        for (int i = 0; i < 10000; i++) {
            router.post("/post/index/" + i).handler(handler);
        }
        for (int i = 0; i < 10000; i++) {
            router.put("/put/index/" + i).handler(handler);
        }
        for (int i = 0; i < 10000; i++) {
            router.delete("/delete/index/" + i).handler(handler);
        }

        String path = "/delete/index/4000";
        route(router, HttpMethod.DELETE, path);

        path = "/get/index/4000";
        route(router, HttpMethod.GET, path);

        path = "/get/index/1";
        route(router, HttpMethod.GET, path);

        path = "/post/index/4000";
        route(router, HttpMethod.POST, path);

        path = "/put/index/8000";
        route(router, HttpMethod.PUT, path);

        path = "/delete/index/8000";
        route(router, HttpMethod.DELETE, path);
        
        path = "/delete/index/4000";
        route(router, HttpMethod.DELETE, path);
    }

    private void route(Router router, HttpMethod method, String path) {
        long st = System.currentTimeMillis();
        router.handle(new HttpServerRequestMock(method, path));
        long et = System.currentTimeMillis();
        System.out.println("route path=" + path + ", " + (et - st) + "ms");
    }

}
