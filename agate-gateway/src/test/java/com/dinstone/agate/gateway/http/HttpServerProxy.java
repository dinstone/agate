package com.dinstone.agate.gateway.http;

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
