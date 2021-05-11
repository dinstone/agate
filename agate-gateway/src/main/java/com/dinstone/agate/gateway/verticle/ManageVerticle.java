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
package com.dinstone.agate.gateway.verticle;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.AddressConstant;
import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.http.RestfulUtil;
import com.dinstone.agate.gateway.utils.NetworkUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * the web endpoint of managing APP and API deployment.
 * 
 * @author dinstone
 *
 */
public class ManageVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ManageVerticle.class);

    private static final int DEFAULT_PORT = 5454;

    private ApplicationContext applicationContext;

    private HttpServerOptions serverOptions;

    private ConsulClient consulClient;

    private Map<String, String> gatewayNode;

    private String serviceId;

    public ManageVerticle(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        // init server options
        serverOptions = new HttpServerOptions().setIdleTimeout(180);
        JsonObject mconfig = config().getJsonObject("manage");
        if (mconfig == null) {
            mconfig = new JsonObject();
        }
        try {
            if (mconfig.getString("host") == null) {
                List<InetAddress> pas = NetworkUtil.getPrivateAddresses();
                if (pas != null && pas.size() > 0) {
                    serverOptions.setHost(pas.get(0).getHostAddress());
                }
            } else {
                serverOptions.setHost(mconfig.getString("host"));
            }
        } catch (Exception e) {
            LOG.warn("unkown host, use default ip 0.0.0.0", e);
        }
        serverOptions.setPort(mconfig.getInteger("port", DEFAULT_PORT));

        // init consul client
        consulClient = ConsulClient.create(vertx, applicationContext.getConsulOptions());

        // init service id
        String clusterCode = applicationContext.getClusterCode();
        serviceId = clusterCode + "$" + serverOptions.getHost() + ":" + serverOptions.getPort();

        // init gateway node
        gatewayNode = new HashMap<String, String>();
        gatewayNode.put("instanceId", serviceId);
        gatewayNode.put("clusterCode", clusterCode);
        gatewayNode.put("manageHost", serverOptions.getHost());
        gatewayNode.put("managePort", String.valueOf(serverOptions.getPort()));
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        launch().compose(server -> regist(server)).onComplete(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        consulClient.deregisterService(serviceId, stopPromise);
    }

    /**
     * create and start http server
     * 
     * @param promise
     */
    private Future<HttpServer> launch() {
        return Future.future(promise -> {
            vertx.createHttpServer(serverOptions).requestHandler(createRouter()).listen(ar -> {
                if (ar.succeeded()) {
                    LOG.info("manage verticle start success, {}:{}", serverOptions.getHost(), serverOptions.getPort());
                    promise.complete(ar.result());
                } else {
                    LOG.error("manage verticle start failed, {}:{}", serverOptions.getHost(), serverOptions.getPort());
                    promise.fail(ar.cause());
                }
            });
        });
    }

    /**
     * regist service
     * 
     * @param server
     * 
     * @return
     */
    private Future<Void> regist(HttpServer server) {
        return Future.future(promise -> {
            String url = "http://" + serverOptions.getHost() + ":" + serverOptions.getPort() + "/health";
            CheckOptions checkOptions = new CheckOptions().setId(serviceId).setName("gateway health check").setHttp(url)
                    .setInterval("30s").setDeregisterAfter("2m");
            ServiceOptions serviceOptions = new ServiceOptions().setId(serviceId).setName("agate-gateway")
                    .setAddress(serverOptions.getHost()).setPort(serverOptions.getPort()).setMeta(gatewayNode)
                    .setCheckOptions(checkOptions);
            consulClient.registerService(serviceOptions, ar -> {
                if (ar.succeeded()) {
                    LOG.info("gateway regist  success {}", serviceId);
                    promise.complete();
                } else {
                    LOG.error("gateway regist failed {}", serviceId);
                    promise.fail(ar.cause());
                }
            });
        });
    }

    private Router createRouter() {
        Router mainRouter = Router.router(vertx);
        mainRouter.route().failureHandler(failureHandler()).handler(BodyHandler.create(false));
        mainRouter.route("/health").handler(rc -> {
            rc.end("OK");
        });

        //
        // APP start and close
        //
        mainRouter.post("/app/start").consumes("application/json").handler(this::gatewayStart);
        mainRouter.delete("/app/close").consumes("application/json").handler(this::gatewayClose);

        //
        // API deploy and remove
        //
        mainRouter.post("/api/deploy").consumes("application/json").handler(this::apiDeploy);
        mainRouter.delete("/api/remove").consumes("application/json").handler(this::apiRemove);

        //
        // metrics
        //
        mainRouter.get("/apm/metrics").handler(this::metrics);

        return mainRouter;
    }

    private Handler<RoutingContext> failureHandler() {
        return rc -> {
            LOG.error("failure handle for {}, {}:{}", rc.request().path(), rc.statusCode(), rc.failure());
            int statusCode = rc.statusCode();
            if (statusCode == -1) {
                statusCode = 500;
            }
            if (!rc.response().ended()) {
                RestfulUtil.exception(rc, statusCode, rc.failure());
            }
        };
    }

    private void gatewayStart(RoutingContext rc) {
        try {
            vertx.eventBus().request(AddressConstant.GATEWAY_START, rc.getBodyAsJson(), ar -> {
                if (ar.succeeded()) {
                    RestfulUtil.success(rc);
                } else {
                    RestfulUtil.failure(rc, ar.cause());
                }
            });
        } catch (Exception e) {
            RestfulUtil.failure(rc, e);
        }
    }

    private void gatewayClose(RoutingContext rc) {
        try {
            vertx.eventBus().request(AddressConstant.GATEWAY_CLOSE, rc.getBodyAsJson(), ar -> {
                if (ar.succeeded()) {
                    RestfulUtil.success(rc);
                } else {
                    RestfulUtil.failure(rc, ar.cause());
                }
            });
        } catch (Exception e) {
            RestfulUtil.failure(rc, e);
        }
    }

    private void apiDeploy(RoutingContext rc) {
        try {
            JsonObject message = rc.getBodyAsJson();
            vertx.eventBus().request(AddressConstant.API_DEPLOY, message, ar -> {
                if (ar.succeeded()) {
                    RestfulUtil.success(rc);
                } else {
                    RestfulUtil.failure(rc, ar.cause());
                }
            });
        } catch (Exception e) {
            RestfulUtil.failure(rc, e);
        }
    }

    private void apiRemove(RoutingContext rc) {
        try {
            JsonObject message = rc.getBodyAsJson();
            vertx.eventBus().request(AddressConstant.API_REMOVE, message, ar -> {
                if (ar.succeeded()) {
                    RestfulUtil.success(rc);
                } else {
                    RestfulUtil.failure(rc, ar.cause());
                }
            });
        } catch (Exception e) {
            RestfulUtil.failure(rc, e);
        }
    }

    private void metrics(RoutingContext rc) {
        try {
            String message = rc.request().getParam("mn");
            if (message == null) {
                RestfulUtil.failure(rc, "mn is null");
                return;
            }
            vertx.eventBus().request(AddressConstant.APM_METRICS, message, ar -> {
                if (ar.succeeded()) {
                    RestfulUtil.success(rc, ar.result().body());
                } else {
                    RestfulUtil.failure(rc, ar.cause());
                }
            });
        } catch (Exception e) {
            RestfulUtil.failure(rc, e);
        }
    }

}
