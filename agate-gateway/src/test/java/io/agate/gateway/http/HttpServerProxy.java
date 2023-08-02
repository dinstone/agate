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
package io.agate.gateway.http;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;

public class HttpServerProxy {

    public static void main(String[] args) {
        // disable DnsResolver
        System.setProperty("vertx.disableDnsResolver", "true");

        Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckInterval(999999999));

        HttpClientOptions options = new HttpClientOptions().setMaxPoolSize(1).setMaxWaitQueueSize(2);
        HttpClient client = vertx.createHttpClient(options);

        // error case:
        // HttpProxy httpProxy = HttpProxy.reverseProxy(client)
        // .originSelector(req -> Future.succeededFuture(SocketAddress.inetSocketAddress(443, "vertx.io")));

        HttpProxy httpProxy = HttpProxy.reverseProxy(client).originRequestProvider((sreq, hclient) -> {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.setServer(SocketAddress.inetSocketAddress(443, "vertx.io"));
            requestOptions.setSsl(Boolean.TRUE).setTimeout(3000).putHeader("Host", "vertx.io");
            return hclient.request(requestOptions);
        });
        httpProxy.addInterceptor(new ProxyInterceptor() {

            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                System.out.println(context.request().headers().remove("Host"));
                System.out.println("request is " + context.request().absoluteURI());
                return context.sendRequest();
            }

            @Override
            public Future<Void> handleProxyResponse(ProxyContext context) {
                System.out.println("response is " + context.response().getStatusCode());
                return context.sendResponse();
            }
        });

        vertx.createHttpServer().requestHandler(httpProxy).listen(8888);

    }

}
