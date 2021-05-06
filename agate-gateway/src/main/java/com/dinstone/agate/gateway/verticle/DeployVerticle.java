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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.AddressConstant;
import com.dinstone.agate.gateway.context.AgateVerticleFactory;
import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.deploy.ApiDeploy;
import com.dinstone.agate.gateway.deploy.Deployer;
import com.dinstone.agate.gateway.deploy.Deployment;
import com.dinstone.agate.gateway.deploy.GatewayDeploy;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.GatewayOptions;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.KeyValue;
import io.vertx.ext.consul.KeyValueList;
import io.vertx.ext.consul.Watch;
import io.vertx.ext.consul.WatchResult;

/**
 * execute the deploy command of APP and API.
 * 
 * @author dinstone
 *
 */
public class DeployVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(DeployVerticle.class);

    private String clusterCode;

    private Deployment deployment;

    private ConsulClientOptions consulClientOptions;

    private Map<String, Watch<KeyValueList>> apiWatchMap = new ConcurrentHashMap<>();

    public DeployVerticle(ApplicationContext appContext) {
        this.clusterCode = appContext.getClusterCode();
        this.deployment = appContext.getDeployment();
        this.consulClientOptions = new ConsulClientOptions(appContext.getConsulOptions()).setTimeout(0);
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer(AddressConstant.APP_START, this::startGateway);
        vertx.eventBus().consumer(AddressConstant.APP_CLOSE, this::closeGateway);
        vertx.eventBus().consumer(AddressConstant.API_DEPLOY, this::deployApiRoute);
        vertx.eventBus().consumer(AddressConstant.API_REMOVE, this::removeApiRoute);

        startPromise.complete();
    }

    private void startGateway(Message<JsonObject> message) {
        GatewayOptions appParam = new GatewayOptions(message.body());
        String error = checkGatewayParams(appParam);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String gatewayName = appParam.getGateway();
        // check app is start
        if (deployment.get(gatewayName) != null) {
            message.reply(null);
            return;
        }
        deployment.put(new GatewayDeploy(gatewayName));

        int instances = config().getInteger("instances", Runtime.getRuntime().availableProcessors());
        DeploymentOptions options = new DeploymentOptions().setConfig(message.body()).setInstances(instances);
        vertx.deployVerticle(AgateVerticleFactory.verticleName(ServerVerticle.class), options, ar -> {
            if (ar.succeeded()) {
                deployment.get(gatewayName).setDeployId(ar.result());
                registApiWatch(gatewayName);

                message.reply(null);
            } else {
                LOG.warn("depley App[{}] failure", gatewayName, ar.cause());

                deployment.remove(gatewayName);

                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    private void registApiWatch(String gatewayName) {
        Watch<KeyValueList> apiWatch = Watch.keyPrefix("agate/apis/" + gatewayName, vertx, consulClientOptions);
        apiWatch.setHandler(ar -> {
            try {
                watchEventHandle(ar);
            } catch (Exception e) {
                LOG.warn("handle api watch event error", e);
            }
        }).start();
        apiWatchMap.put(gatewayName, apiWatch);
    }

    private void watchEventHandle(WatchResult<KeyValueList> wr) {
        if (!wr.succeeded()) {
            LOG.warn("api watch event error", wr.cause());
            return;
        }

        Map<String, KeyValue> pkvMap = new HashMap<String, KeyValue>();
        if (wr.prevResult() != null && wr.prevResult().getList() != null) {
            wr.prevResult().getList().forEach(kv -> pkvMap.put(kv.getKey(), kv));
        }

        Map<String, KeyValue> nkvMap = new HashMap<String, KeyValue>();
        if (wr.nextResult() != null && wr.nextResult().getList() != null) {
            wr.nextResult().getList().forEach(kv -> nkvMap.put(kv.getKey(), kv));
        }

        // create: next have and prev not;
        // update: next have and prev have, modify index not equal
        List<KeyValue> cList = new LinkedList<KeyValue>();
        List<KeyValue> uList = new LinkedList<KeyValue>();
        nkvMap.forEach((k, nkv) -> {
            KeyValue pkv = pkvMap.get(k);
            if (pkv == null) {
                cList.add(nkv);
            } else if (pkv.getModifyIndex() != nkv.getModifyIndex()) {
                uList.add(nkv);
            }
        });

        // delete: prev have and next not;
        List<KeyValue> dList = new LinkedList<KeyValue>();
        pkvMap.forEach((k, pkv) -> {
            if (!nkvMap.containsKey(k)) {
                dList.add(pkv);
            }
        });

        uList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().request(AddressConstant.API_REMOVE, message, ar -> {
                    if (ar.succeeded()) {
                        vertx.eventBus().send(AddressConstant.API_DEPLOY, message);
                    }
                });
            } catch (Exception e) {
                LOG.warn("api message is error", e);
            }
        });
        dList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.API_REMOVE, message);
            } catch (Exception e) {
                LOG.warn("api message is error", e);
            }
        });
        cList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.API_DEPLOY, message);
            } catch (Exception e) {
                LOG.warn("api message is error", e);
            }
        });
    }

    private void removeApiWatch(String appName) {
        Watch<KeyValueList> apiWatch = apiWatchMap.remove(appName);
        if (apiWatch != null) {
            apiWatch.stop();
        }
    }

    private void closeGateway(Message<JsonObject> message) {
        GatewayOptions appParam = new GatewayOptions(message.body());
        String error = checkGatewayParams(appParam);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String appName = appParam.getGateway();
        GatewayDeploy appDeploy = deployment.get(appName);
        if (appDeploy == null || appDeploy.getDeployId() == null) {
            message.reply(null);
            return;
        }

        vertx.undeploy(appDeploy.getDeployId(), ar -> {
            if (ar.succeeded()) {
                deployment.remove(appName);
                removeApiWatch(appName);

                message.reply(null);
            } else {
                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void deployApiRoute(Message<JsonObject> message) {
        ApiOptions api = new ApiOptions(message.body());
        String error = checkApiParam(api);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        GatewayDeploy appDeploy = deployment.get(api.getGateway());
        if (appDeploy == null) {
            message.fail(503, "gateway is not start");
            return;
        }
        if (appDeploy.containApi(api.getApiName())) {
            message.reply(null);
            return;
        }

        List<Future> futures = new LinkedList<Future>();
        for (Deployer deployer : appDeploy.getApiDeployers()) {
            futures.add(deployer.deployApi(api));
        }
        CompositeFuture.all(futures).onComplete(ar -> {
            if (ar.succeeded()) {
                LOG.info("deploy api success : {} / {}", api.getGateway(), api.getApiName());

                ApiDeploy apiDeploy = new ApiDeploy();
                apiDeploy.setName(api.getApiName());
                apiDeploy.setPrefix(api.getRequest().getPrefix());
                apiDeploy.setPath(api.getRequest().getPath());
                appDeploy.registApi(apiDeploy);

                message.reply(null);
            } else {
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void removeApiRoute(Message<JsonObject> message) {
        ApiOptions api = new ApiOptions(message.body());
        String error = checkApiParam(api);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        GatewayDeploy appDeploy = deployment.get(api.getGateway());
        if (appDeploy == null || !appDeploy.containApi(api.getApiName())) {
            message.reply(null);
            return;
        }

        List<Future> futures = new LinkedList<Future>();
        for (Deployer deployer : appDeploy.getApiDeployers()) {
            futures.add(deployer.removeApi(api));
        }
        CompositeFuture.all(futures).onComplete(ar -> {
            if (ar.succeeded()) {
                LOG.info("remove api success : {} / {}", api.getGateway(), api.getApiName());

                appDeploy.removeApi(api.getApiName());
                message.reply(null);
            } else {
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

    private String checkApiParam(ApiOptions apiParam) {
        if (apiParam == null) {
            return "API options is null";
        }
        if (!this.clusterCode.equals(apiParam.getCluster())) {
            return "gateway cluster is not same with " + clusterCode;
        }
        if (apiParam.getGateway() == null || apiParam.getGateway().isEmpty()) {
            return "gateway name is empty";
        }
        if (apiParam.getApiName() == null || apiParam.getApiName().isEmpty()) {
            return "API name is empty";
        }
        return null;
    }

}
