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
package io.agate.gateway.http;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.httpproxy.HttpProxy;

public class HttpServerProxy {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(999999999));

        HttpClientOptions options = new HttpClientOptions().setMaxPoolSize(1).setMaxWaitQueueSize(2);
        HttpClient client = vertx.createHttpClient(options);

        HttpProxy httpProxy = HttpProxy.reverseProxy(client).origin(80, "vertx.io");
        // Router proxyRouter = Router.router(vertx);
        // proxyRouter.route().handler(rc -> {
        // httpProxy.handle(rc.request());
        // });

        vertx.createHttpServer().requestHandler(httpProxy).listen(8888);

    }

}
