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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.AddressConstant;
import io.agate.gateway.context.AgateVerticleFactory;
import io.agate.gateway.context.ApplicationContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.KeyValue;
import io.vertx.ext.consul.KeyValueList;
import io.vertx.ext.consul.Watch;
import io.vertx.ext.consul.WatchResult;

/**
 * launch the verticles and init gateway context.
 * 
 * @author dinstone
 */
public class LaunchVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchVerticle.class);

    private ApplicationContext appContext;

    private Watch<KeyValueList> clusterWatch;

    public LaunchVerticle(ApplicationContext context) {
        this.appContext = context;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = config();

        // system verticle
        DeploymentOptions svdOptions = new DeploymentOptions().setConfig(config).setInstances(1);
        Future<String> svf = deploy(AgateVerticleFactory.verticleName(SystemVerticle.class), svdOptions);

        // deploy verticle
        DeploymentOptions dvdOptions = new DeploymentOptions().setConfig(config).setInstances(1);
        Future<String> dvf = deploy(AgateVerticleFactory.verticleName(DeployVerticle.class), dvdOptions);

        CompositeFuture.all(svf, dvf).compose(f -> manage()).compose(f -> watch()).onComplete(ar -> {
            if (ar.succeeded()) {
                startPromise.complete();
            } else {
                destroy();
                startPromise.fail(ar.cause());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        destroy();
    }

    private void destroy() {
        if (clusterWatch != null) {
            clusterWatch.stop();
        }
    }

    private Future<String> deploy(String verticleName, DeploymentOptions deployOptions) {
        return Future.future(promise -> {
            vertx.deployVerticle(verticleName, deployOptions, promise);
        });
    }

    private Future<String> manage() {
        DeploymentOptions mvdOptions = new DeploymentOptions().setConfig(config()).setInstances(1);
        return deploy(AgateVerticleFactory.verticleName(ManageVerticle.class), mvdOptions);
    }

    /**
     * Watch config and deploy
     * 
     * @return
     */
    private Future<Void> watch() {
        return Future.future(p -> {
            clusterWatch = Watch.keyPrefix("agate/gateway/" + appContext.getClusterCode(), vertx,
                new ConsulClientOptions(appContext.getConsulOptions()).setTimeout(0)).setHandler(ar -> {
                    try {
                        watchGatewayEventHandle(ar);
                    } catch (Exception e) {
                        LOG.warn("handle gateway watch event error", e);
                    }
                }).start();
            p.complete();
        });
    }

    private void watchGatewayEventHandle(WatchResult<KeyValueList> wr) {
        if (!wr.succeeded()) {
            LOG.warn("gateway watch event error", wr.cause());
            return;
        }

        Map<String, KeyValue> pkvMap = new HashMap<String, KeyValue>();
        if (wr.prevResult() != null && wr.prevResult().getList() != null) {
            wr.prevResult().getList().forEach(kv -> {
                if (kv.getValue() != null && kv.getValue().length() > 0) {
                    pkvMap.put(kv.getKey(), kv);
                }
            });
        }

        Map<String, KeyValue> nkvMap = new HashMap<String, KeyValue>();
        if (wr.nextResult() != null && wr.nextResult().getList() != null) {
            wr.nextResult().getList().forEach(kv -> {
                if (kv.getValue() != null && kv.getValue().length() > 0) {
                    nkvMap.put(kv.getKey(), kv);
                }
            });
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
                vertx.eventBus().request(AddressConstant.GATEWAY_CLOSE, message, ar -> {
                    if (ar.succeeded()) {
                        vertx.eventBus().send(AddressConstant.GATEWAY_START, message);
                    }
                });
            } catch (Exception e) {
                LOG.warn("gateway message is error", e);
            }
        });
        dList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.GATEWAY_CLOSE, message);
            } catch (Exception e) {
                LOG.warn("gateway message is error", e);
            }
        });
        cList.forEach(kv -> {
            try {
                JsonObject message = new JsonObject(kv.getValue());
                vertx.eventBus().send(AddressConstant.GATEWAY_START, message);
            } catch (Exception e) {
                LOG.warn("gateway message is error", e);
            }
        });
    }

}
