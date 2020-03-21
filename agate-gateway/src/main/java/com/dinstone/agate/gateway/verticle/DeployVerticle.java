/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.AddressConstant;
import com.dinstone.agate.gateway.context.AgateVerticleFactory;
import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.deploy.ApiDeploy;
import com.dinstone.agate.gateway.deploy.AppDeploy;
import com.dinstone.agate.gateway.deploy.Deployer;
import com.dinstone.agate.gateway.deploy.Deployment;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.AppOptions;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * execute the deploy command of APP and API.
 * 
 * @author dinstone
 *
 */
public class DeployVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(DeployVerticle.class);

    private Deployment deployment;

    private String clusterId;

    public DeployVerticle(ApplicationContext applicationContext) {
        this.deployment = applicationContext.getDeployment();
        this.clusterId = applicationContext.getClusterId();
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer(AddressConstant.APP_START, this::startApp);
        vertx.eventBus().consumer(AddressConstant.APP_CLOSE, this::closeApp);
        vertx.eventBus().consumer(AddressConstant.API_DEPLOY, this::deployApi);
        vertx.eventBus().consumer(AddressConstant.API_REMOVE, this::removeApi);

        startPromise.complete();
    }

    private void startApp(Message<JsonObject> message) {
        AppOptions appParam = new AppOptions(message.body());
        String error = checkAppParam(appParam);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String appName = appParam.getAppName();
        // check app is start
        if (deployment.get(appName) != null) {
            message.reply(null);
            return;
        }
        deployment.put(new AppDeploy(appName));

        int instances = config().getInteger("instances", Runtime.getRuntime().availableProcessors());
        DeploymentOptions options = new DeploymentOptions().setConfig(message.body()).setInstances(instances);
        vertx.deployVerticle(AgateVerticleFactory.appendPrefix(ServerVerticle.class), options, ar -> {
            if (ar.succeeded()) {
                deployment.get(appName).setDeployId(ar.result());

                message.reply(null);
            } else {
                deployment.remove(appName);

                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    private void closeApp(Message<JsonObject> message) {
        AppOptions appParam = new AppOptions(message.body());
        String error = checkAppParam(appParam);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        String appName = appParam.getAppName();
        AppDeploy appDeploy = deployment.get(appName);
        if (appDeploy == null || appDeploy.getDeployId() == null) {
            message.reply(null);
            return;
        }

        vertx.undeploy(appDeploy.getDeployId(), ar -> {
            if (ar.succeeded()) {
                deployment.remove(appName);

                message.reply(null);
            } else {
                message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void deployApi(Message<JsonObject> message) {
        ApiOptions api = new ApiOptions(message.body());
        String error = checkApiParam(api);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        AppDeploy appDeploy = deployment.get(api.getAppName());
        if (appDeploy == null) {
            message.fail(503, "APP is not start");
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
        CompositeFuture.all(futures).setHandler(ar -> {
            if (ar.succeeded()) {
                LOG.info("deploy api success {}", api.getApiName());

                ApiDeploy apiDeploy = new ApiDeploy();
                apiDeploy.setName(api.getApiName());
                apiDeploy.setPrefix(api.getPrefix());
                apiDeploy.setPath(api.getPath());
                appDeploy.registApi(apiDeploy);

                message.reply(null);
            } else {
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void removeApi(Message<JsonObject> message) {
        ApiOptions api = new ApiOptions(message.body());
        String error = checkApiParam(api);
        if (error != null) {
            message.fail(400, error);
            return;
        }

        AppDeploy appDeploy = deployment.get(api.getAppName());
        if (appDeploy == null || !appDeploy.containApi(api.getApiName())) {
            message.reply(null);
            return;
        }

        List<Future> futures = new LinkedList<Future>();
        for (Deployer deployer : appDeploy.getApiDeployers()) {
            futures.add(deployer.removeApi(api));
        }
        CompositeFuture.all(futures).setHandler(ar -> {
            if (ar.succeeded()) {
                LOG.info("remove api success {}", api.getApiName());

                appDeploy.removeApi(api.getApiName());
                message.reply(null);
            } else {
                message.fail(500, ar.cause().getMessage());
            }
        });
    }

    private String checkAppParam(AppOptions appParam) {
        if (appParam == null) {
            return "APP parameter is null";
        }
        if (!this.clusterId.equals(appParam.getCluster())) {
            return "APP cluster is not same with " + clusterId;
        }
        if (appParam.getAppName() == null || appParam.getAppName().isEmpty()) {
            return "APP name is empty";
        }
        return null;
    }

    private String checkApiParam(ApiOptions apiParam) {
        if (apiParam == null) {
            return "API parameter is null";
        }
        if (!this.clusterId.equals(apiParam.getCluster())) {
            return "APP cluster is not same with " + clusterId;
        }
        if (apiParam.getAppName() == null || apiParam.getAppName().isEmpty()) {
            return "APP name is empty";
        }
        if (apiParam.getApiName() == null || apiParam.getApiName().isEmpty()) {
            return "API name is empty";
        }
        return null;
    }

}
