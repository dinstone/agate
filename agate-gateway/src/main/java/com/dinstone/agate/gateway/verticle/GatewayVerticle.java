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

package com.dinstone.agate.gateway.verticle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.deploy.RouteDeploy;
import com.dinstone.agate.gateway.handler.FailureHandler;
import com.dinstone.agate.gateway.handler.OperationHandler;
import com.dinstone.agate.gateway.handler.internal.AccessLogHandler;
import com.dinstone.agate.gateway.http.HttpUtil;
import com.dinstone.agate.gateway.http.RestfulUtil;
import com.dinstone.agate.gateway.options.GatewayOptions;
import com.dinstone.agate.gateway.options.RequestOptions;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.plugin.RoutePlugin;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * the GatewayVerticle is Gateway instance, it route request to backend service.
 * 
 * @author dinstone
 */
public class GatewayVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayVerticle.class);

    private Map<String, Route> routeRouteMap = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    private HttpServerOptions serverOptions;

    private Router mainRouter;

    private String gatewayName;

    public GatewayVerticle(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        GatewayOptions gatewayOptions = new GatewayOptions(config());
        gatewayName = gatewayOptions.getGateway();

        serverOptions = gatewayOptions.getServerOptions();
        if (serverOptions == null) {
            serverOptions = new HttpServerOptions();
        }
        String host = serverOptions.getHost();
        if (host == null || host.isEmpty() || "*".equals(host)) {
            serverOptions.setHost(NetServerOptions.DEFAULT_HOST);
        }
        if (serverOptions.getIdleTimeout() == 0) {
            serverOptions.setIdleTimeout(60);
        }
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        mainRouter = createHttpServerRouter();
        vertx.createHttpServer(serverOptions).requestHandler(mainRouter).listen(ar -> {
            if (ar.succeeded()) {
                // register deployer
                registDeployer();

                LOG.info("gateway verticle start success, {}/{}:{}", gatewayName, serverOptions.getHost(),
                        serverOptions.getPort());
                startPromise.complete();
            } else {
                LOG.error("gateway verticle start failed, {}/{}:{}", gatewayName, serverOptions.getHost(),
                        serverOptions.getPort());
                startPromise.fail(ar.cause());
            }
        });

    }

    @Override
    public void stop() throws Exception {
        removeDeployer();

        LOG.info("gateway verticle stop success, {}/{}:{}", gatewayName, serverOptions.getHost(),
                serverOptions.getPort());
    }

    public Future<Void> deployRoute(RouteDeploy deploy) {
        Promise<Void> promise = Promise.promise();
        context.runOnContext(ar -> {
            RouteOptions routeOptions = deploy.getRouteOptions();
            try {
                if (routeRouteMap.containsKey(routeOptions.getRoute())) {
                    promise.complete();
                    return;
                }

                RequestOptions requestOptions = routeOptions.getRequest();
                // create sub router
                Router subRouter = Router.router(vertx);
                // create route
                Route route = null;
                if (HttpUtil.pathIsRegex(requestOptions.getPath())) {
                    route = subRouter.routeWithRegex(requestOptions.getPath());
                } else {
                    route = subRouter.route(requestOptions.getPath());
                }
                // method
                String method = requestOptions.getMethod();
                if (method != null && method.length() > 0) {
                    route.method(HttpMethod.valueOf(method.toUpperCase()));
                }
                // consumes
                if (requestOptions.getConsumes() != null) {
                    for (String consume : requestOptions.getConsumes()) {
                        route.consumes(consume);
                    }
                }
                // produces
                if (requestOptions.getProduces() != null) {
                    for (String produce : requestOptions.getProduces()) {
                        route.produces(produce);
                    }
                }

                // // before handler: tracing handler
                // route.handler(new ZipkinTracingHandler(routeOptions));
                //
                // // before handler: metrics handler
                // MeterRegistry meterRegistry = BackendRegistries.getDefaultNow();
                // if (meterRegistry != null) {
                // route.handler(new MeterMetricsHandler(routeOptions, meterRegistry));
                // }
                //
                // if (routeOptions.getPlugins() != null) {
                // // before handler : rate limit handler
                // route.handler(new RateLimitHandler(routeOptions));
                // // before handler: circuit breaker handler
                // route.handler(CircuitBreakerHandler.create(deploy, vertx));
                // }
                //
                // // routing handler
                // route.handler(HttpProxyHandler.create(deploy, vertx));
                //
                // // after handler : result reply handler
                // route.handler(new ResultReplyHandler(routeOptions));

                // failure handler
                // route.failureHandler(new RestfulFailureHandler(routeOptions));

                for (RoutePlugin routePlugin : deploy.getRoutePlugins()) {
                    OperationHandler handler = routePlugin.createHandler(vertx);
                    if (handler instanceof FailureHandler) {
                        route.failureHandler(handler);
                    } else {
                        route.handler(handler);
                    }
                }

                String mountPoint = "/";
                String prefix = requestOptions.getPrefix();
                if (prefix != null && prefix.startsWith("/")) {
                    mountPoint = prefix;
                }
                // mount sub router
                Route mountedRoute = mountRouter(mountPoint, subRouter);
                // cache route
                routeRouteMap.put(routeOptions.getRoute(), mountedRoute);

                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });

        return promise.future();
    }

    public Future<Void> removeRoute(RouteDeploy deploy) {
        Promise<Void> promise = Promise.promise();
        context.runOnContext(ar -> {
            try {
                Route route = routeRouteMap.remove(deploy.getRoute());
                if (route != null) {
                    route.disable().remove();
                }
                promise.complete();
            } catch (Exception e) {
                promise.fail(e);
            }
        });

        return promise.future();
    }

    /**
     * mount sub router to main router
     * 
     * @param mountPoint
     * @param subRouter
     * 
     * @return
     */
    private Route mountRouter(String mountPoint, Router subRouter) {
        return mainRouter.mountSubRouter(mountPoint, subRouter);
    }

    private void registDeployer() {
        applicationContext.getClusterDeploy().get(gatewayName).regist(this);
    }

    private void removeDeployer() {
        applicationContext.getClusterDeploy().get(gatewayName).remove(this);
    }

    @SuppressWarnings("unused")
    private SessionHandler sessionHandler() {
        return SessionHandler.create(LocalSessionStore.create(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, 60000))
                .setNagHttps(false);
    }

    private Router createHttpServerRouter() {
        Router mainRouter = Router.router(vertx);
        // error handler
        mainRouter.errorHandler(400, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "can’t accept an empty body");
        });
        mainRouter.errorHandler(404, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "no route matches the path " + rc.request().path());
        });
        mainRouter.errorHandler(405, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "don’t match the HTTP Method " + rc.request().method());
        });
        mainRouter.errorHandler(406, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(),
                    "can’t provide a response with a content type matching Accept header");
        });
        mainRouter.errorHandler(415, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "can’t accept the Content-type");
        });

        mainRouter.route().handler(new AccessLogHandler());
        mainRouter.route().handler(ResponseTimeHandler.create());
        return mainRouter;
    }

}
