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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.AddressConstant;
import io.agate.gateway.context.AgateVerticleFactory;
import io.agate.gateway.context.ApplicationContext;
import io.agate.gateway.deploy.ClusterDeploy;
import io.agate.gateway.deploy.GatewayDeploy;
import io.agate.gateway.deploy.RouteDeploy;
import io.agate.gateway.options.GatewayOptions;
import io.agate.gateway.options.RouteOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.KeyValue;
import io.vertx.ext.consul.KeyValueList;
import io.vertx.ext.consul.Watch;
import io.vertx.ext.consul.WatchResult;

/**
 * execute the deployment command of Gateway and Route.
 *
 * @author dinstone
 */
public class DeployVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(DeployVerticle.class);

    private final Map<String, Watch<KeyValueList>> gatewayRouteWatches = new ConcurrentHashMap<>();

    private final ApplicationContext appContext;

    private final ClusterDeploy clusterDeploy;

    private final String clusterCode;

    public DeployVerticle(ApplicationContext appContext) {
        this.appContext = appContext;
        this.clusterCode = appContext.getClusterCode();
        this.clusterDeploy = appContext.getClusterDeploy();
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(AddressConstant.GATEWAY_START, this::startGateway);
        vertx.eventBus().consumer(AddressConstant.GATEWAY_CLOSE, this::closeGateway);

        vertx.eventBus().consumer(AddressConstant.ROUTE_DEPLOY, this::deployRoute);
        vertx.eventBus().consumer(AddressConstant.ROUTE_UPDATE, this::updateRoute);
        vertx.eventBus().consumer(AddressConstant.ROUTE_REMOVE, this::removeRoute);
    }

    private void startGateway(Message<JsonObject> message) {
        GatewayOptions gatewayOptions = new GatewayOptions(message.body());
        String error = checkGatewayParams(gatewayOptions);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String gatewayName = gatewayOptions.getGateway();
        // check gateway is deployed
        if (clusterDeploy.get(gatewayName) != null) {
            message.reply(false);
            return;
        }
        clusterDeploy.put(new GatewayDeploy(gatewayOptions));

        // deploy gateway
        int instances = config().getInteger("instances", Runtime.getRuntime().availableProcessors());
        DeploymentOptions options = new DeploymentOptions().setConfig(message.body()).setInstances(instances);
        vertx.deployVerticle(AgateVerticleFactory.verticleName(GatewayVerticle.class), options, ar -> {
            if (ar.succeeded()) {
                clusterDeploy.get(gatewayName).setDeployId(ar.result());
                // watch gateway's route for deploying
                registerRouteWatch(gatewayName);

                message.reply(true);
                LOG.info("start gateway success : {}", gatewayName);
            } else {
                LOG.warn("start gateway failure : {}, cause {}", gatewayName, ar.cause());

                clusterDeploy.remove(gatewayName);
                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    private void closeGateway(Message<JsonObject> message) {
        GatewayOptions gatewayOptions = new GatewayOptions(message.body());
        String error = checkGatewayParams(gatewayOptions);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String gatewayName = gatewayOptions.getGateway();
        GatewayDeploy gatewayDeploy = clusterDeploy.get(gatewayName);
        if (gatewayDeploy == null || gatewayDeploy.getDeployId() == null) {
            message.reply(false);
            return;
        }

        vertx.undeploy(gatewayDeploy.getDeployId(), ar -> {
            if (ar.succeeded()) {
                clusterDeploy.remove(gatewayName);
                removeRouteWatch(gatewayName);
                message.reply(true);
                LOG.info("close gateway success : {}", gatewayName);
            } else {
                LOG.warn("close gateway failure : {}, cause {}", gatewayName, ar.cause());
                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    private void registerRouteWatch(String gatewayName) {
        ConsulClientOptions clientOptions = new ConsulClientOptions(appContext.getConsulOptions()).setTimeout(0);
        Watch<KeyValueList> routeWatch = Watch
                .keyPrefix("agate/route/" + appContext.getClusterCode() + "/" + gatewayName, vertx, clientOptions);
        routeWatch.setHandler(ar -> {
            try {
                watchRouteEventHandle(ar);
            } catch (Exception e) {
                LOG.warn("handle route watch event error", e);
            }
        }).start();
        gatewayRouteWatches.put(gatewayName, routeWatch);
    }

    private void watchRouteEventHandle(WatchResult<KeyValueList> wr) {
        if (!wr.succeeded()) {
            LOG.warn("route watch event error", wr.cause());
            return;
        }

        Map<String, KeyValue> pkvMap = new HashMap<>();
        if (wr.prevResult() != null && wr.prevResult().getList() != null) {
            wr.prevResult().getList().forEach(kv -> pkvMap.put(kv.getKey(), kv));
        }

        Map<String, KeyValue> nkvMap = new HashMap<>();
        if (wr.nextResult() != null && wr.nextResult().getList() != null) {
            wr.nextResult().getList().forEach(kv -> nkvMap.put(kv.getKey(), kv));
        }

        // create: next have and prev not;
        // update: next have and prev have, modify index not equal
        List<KeyValue> cList = new LinkedList<>();
        List<KeyValue> uList = new LinkedList<>();
        nkvMap.forEach((k, nkv) -> {
            KeyValue pkv = pkvMap.get(k);
            if (pkv == null) {
                cList.add(nkv);
            } else if (pkv.getModifyIndex() != nkv.getModifyIndex()) {
                uList.add(nkv);
            }
        });

        // delete: prev have and next not;
        List<KeyValue> dList = new LinkedList<>();
        pkvMap.forEach((k, pkv) -> {
            if (!nkvMap.containsKey(k)) {
                dList.add(pkv);
            }
        });

        // update route
        uList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.ROUTE_UPDATE, message);
            } catch (Exception e) {
                LOG.warn("update route message is error", e);
            }
        });
        // delete route
        dList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.ROUTE_REMOVE, message);
            } catch (Exception e) {
                LOG.warn("delete route message is error", e);
            }
        });
        // deploy route
        cList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.ROUTE_DEPLOY, message);
            } catch (Exception e) {
                LOG.warn("route message is error", e);
            }
        });
    }

    private void removeRouteWatch(String gatewayName) {
        Watch<KeyValueList> watch = gatewayRouteWatches.remove(gatewayName);
        if (watch != null) {
            watch.stop();
        }
    }

    private void deployRoute(Message<JsonObject> message) {
        RouteOptions routeOptions = new RouteOptions(message.body());
        String error = checkRouteParams(routeOptions);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        GatewayDeploy gatewayDeploy = clusterDeploy.get(routeOptions.getGateway());
        if (gatewayDeploy == null) {
            message.fail(503, "gateway is not start");
            return;
        }
        if (gatewayDeploy.hasRoute(routeOptions.getRoute())) {
            message.reply(false);
            return;
        }

        RouteDeploy routeDeploy = new RouteDeploy(appContext, gatewayDeploy.getGatewayOptions(), routeOptions);
        gatewayDeploy.registryRoute(routeDeploy);

        List<Future<?>> futures = new LinkedList<>();
        for (GatewayVerticle gatewayVerticle : gatewayDeploy.getGatewayVerticles()) {
            futures.add(gatewayVerticle.deployRoute(routeDeploy));
        }
        Future.all(futures).onComplete(ar -> {
            if (ar.succeeded()) {
                LOG.info("deploy route success : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.reply(true);
            } else {
                gatewayDeploy.removeRoute(routeDeploy);
                routeDeploy.destroy();
                LOG.warn("deploy route failure : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    private void updateRoute(Message<JsonObject> message) {
        RouteOptions routeOptions = new RouteOptions(message.body());
        String error = checkRouteParams(routeOptions);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        GatewayDeploy gatewayDeploy = clusterDeploy.get(routeOptions.getGateway());
        if (gatewayDeploy == null) {
            message.fail(503, "gateway is not start");
            return;
        }

        RouteDeploy oldDeploy = gatewayDeploy.getRoute(routeOptions.getRoute());
        RouteDeploy newDeploy = new RouteDeploy(appContext, gatewayDeploy.getGatewayOptions(), routeOptions);
        gatewayDeploy.registryRoute(newDeploy);

        List<Future<?>> futures = new LinkedList<>();
        for (GatewayVerticle gatewayVerticle : gatewayDeploy.getGatewayVerticles()) {
            futures.add(gatewayVerticle.deployRoute(newDeploy));
        }
        Future.all(futures).onComplete(ar -> {
            if (ar.succeeded()) {
                if (oldDeploy != null) {
                    oldDeploy.destroy();
                }
                LOG.info("update route success : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.reply(true);
            } else {
                gatewayDeploy.removeRoute(newDeploy);
                if (oldDeploy != null) {
                    gatewayDeploy.registryRoute(oldDeploy);
                }
                LOG.warn("update route failure : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    private void removeRoute(Message<JsonObject> message) {
        RouteOptions routeOptions = new RouteOptions(message.body());
        String error = checkRouteParams(routeOptions);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        GatewayDeploy gatewayDeploy = clusterDeploy.get(routeOptions.getGateway());
        if (gatewayDeploy == null) {
            message.fail(503, "gateway is not start");
            return;
        }
        if (!gatewayDeploy.hasRoute(routeOptions.getRoute())) {
            message.reply(false);
            return;
        }

        RouteDeploy routeDeploy = gatewayDeploy.getRoute(routeOptions.getRoute());
        List<Future<?>> futures = new LinkedList<>();
        for (GatewayVerticle gatewayVerticle : gatewayDeploy.getGatewayVerticles()) {
            futures.add(gatewayVerticle.removeRoute(routeDeploy));
        }
        Future.all(futures).onComplete(ar -> {
            gatewayDeploy.removeRoute(routeDeploy);
            routeDeploy.destroy();
            if (ar.succeeded()) {
                LOG.info("remove route success : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.reply(true);
            } else {
                LOG.warn("remove route failure : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    private String checkGatewayParams(GatewayOptions options) {
        if (options == null) {
            return "Gateway parameter is null";
        }
        if (!this.clusterCode.equals(options.getCluster())) {
            return "Gateway cluster is not same with " + clusterCode;
        }
        if (options.getGateway() == null || options.getGateway().isEmpty()) {
            return "Gateway name is empty";
        }
        return null;
    }

    private String checkRouteParams(RouteOptions routeOptions) {
        if (routeOptions == null) {
            return "route options is null";
        }
        if (!this.clusterCode.equals(routeOptions.getCluster())) {
            return "gateway cluster is not same with " + clusterCode;
        }
        if (routeOptions.getGateway() == null || routeOptions.getGateway().isEmpty()) {
            return "gateway name is empty";
        }
        if (routeOptions.getRoute() == null || routeOptions.getRoute().isEmpty()) {
            return "route name is empty";
        }
        return null;
    }

}
