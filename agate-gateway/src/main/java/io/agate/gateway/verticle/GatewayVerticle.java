/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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
package io.agate.gateway.verticle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.ApplicationContext;
import io.agate.gateway.deploy.RouteDeploy;
import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.http.HttpUtil;
import io.agate.gateway.http.RestfulUtil;
import io.agate.gateway.options.FrontendOptions;
import io.agate.gateway.options.GatewayOptions;
import io.agate.gateway.options.RouteOptions;
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
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * the GatewayVerticle is Gateway instance, which route request to backend service.
 *
 * @author dinstone
 */
public class GatewayVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayVerticle.class);

    private final Map<String, Route> routeMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

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
                registerDeployer();

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
                if (routeMap.containsKey(routeOptions.getRoute())) {
                    promise.complete();
                    return;
                }

                // create sub router
                Router subRouter = Router.router(vertx);
                // create route
                Route route = subRouter.route().setName(routeOptions.getRoute());
                // virtual host
                String domain = routeOptions.getDomain();
                if (domain != null && !domain.isEmpty()) {
                    route.virtualHost(domain);
                }
                // path
                FrontendOptions frontendOptions = routeOptions.getFrontend();
                if (HttpUtil.pathIsRegex(frontendOptions.getPath())) {
                    route = route.pathRegex(frontendOptions.getPath());
                } else {
                    route = route.path(frontendOptions.getPath());
                }
                // method
                String method = frontendOptions.getMethod();
                if (method != null && !method.isEmpty()) {
                    route.method(HttpMethod.valueOf(method.toUpperCase()));
                }
                // consumes
                if (frontendOptions.getConsumes() != null) {
                    for (String consume : frontendOptions.getConsumes()) {
                        if (consume != null && !consume.isEmpty()) {
                            route.consumes(consume);
                        }
                    }
                }
                // produces
                if (frontendOptions.getProduces() != null) {
                    for (String produce : frontendOptions.getProduces()) {
                        if (produce != null && !produce.isEmpty()) {
                            route.produces(produce);
                        }
                    }
                }

                // routing handler
                for (RouteHandler handler : deploy.getRoutingHandlers(vertx)) {
                    route.handler(handler);
                }
                // failure handler
                for (RouteHandler handler : deploy.getFailureHandlers(vertx)) {
                    route.failureHandler(handler);
                }

                String mountPoint = routeOptions.getPrefix();
                if (mountPoint != null && !mountPoint.isEmpty()) {
                    if (!mountPoint.endsWith("*")) {
                        mountPoint = mountPoint + "*";
                    }
                } else {
                    mountPoint = "/*";
                }

                // mount sub router
                Route mountedRoute = mountRouter(mountPoint, subRouter);
                // cache route
                routeMap.put(routeOptions.getRoute(), mountedRoute);

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
                Route route = routeMap.remove(deploy.getRoute());
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

    private void registerDeployer() {
        applicationContext.getClusterDeploy().get(gatewayName).registryVerticle(this);
    }

    private void removeDeployer() {
        applicationContext.getClusterDeploy().get(gatewayName).removeVerticle(this);
    }

    @SuppressWarnings("unused")
    private SessionHandler sessionHandler() {
        return SessionHandler.create(LocalSessionStore.create(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, 60000))
                .setNagHttps(false);
    }

    /**
     * mount sub router to main router
     */
    private Route mountRouter(String mountPoint, Router subRouter) {
        return mainRouter.route(mountPoint).subRouter(subRouter);
    }

    private Router createHttpServerRouter() {
        Router mainRouter = Router.router(vertx);
        // client error handler
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

        // server error handler
        mainRouter.errorHandler(500, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "gateway internal error");
        });
        mainRouter.errorHandler(501, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "gateway can't handle error");
        });
        mainRouter.errorHandler(502, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "no live upstream error");
        });
        mainRouter.errorHandler(503, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "gateway unavailable error");
        });
        mainRouter.errorHandler(504, rc -> {
            RestfulUtil.exception(rc, rc.statusCode(), "connect upstream timeout error");
        });

        return mainRouter;
    }
}
