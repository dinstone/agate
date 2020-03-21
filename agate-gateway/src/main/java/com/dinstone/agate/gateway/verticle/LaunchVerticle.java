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

import com.dinstone.agate.gateway.context.AgateVerticleFactory;
import com.dinstone.agate.gateway.context.ApplicationContext;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * launch the verticles and init gateway context.
 * 
 * @author dinstone
 *
 */
public class LaunchVerticle extends AbstractVerticle {

    private ApplicationContext applicationContext;

    @Override
    public void stop() throws Exception {
        if (applicationContext != null) {
            applicationContext.destroy();
        }
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        applicationContext = new ApplicationContext(config());
        vertx.registerVerticleFactory(new AgateVerticleFactory(applicationContext));

        // system verticle
        DeploymentOptions svdOptions = new DeploymentOptions().setConfig(config()).setInstances(1);
        Future<String> svfuture = deploy(AgateVerticleFactory.appendPrefix(SystemVerticle.class), svdOptions);

        // deploy verticle
        DeploymentOptions dvdOptions = new DeploymentOptions().setConfig(config()).setInstances(1);
        Future<String> dvFuture = deploy(AgateVerticleFactory.appendPrefix(DeployVerticle.class), dvdOptions);

        CompositeFuture.all(svfuture, dvFuture).compose(f -> manage()).compose(cf -> load()).setHandler(startPromise);
    }

    private Future<String> manage() {
        DeploymentOptions mvdOptions = new DeploymentOptions().setConfig(config()).setInstances(1);
        return deploy(AgateVerticleFactory.appendPrefix(ManageVerticle.class), mvdOptions);
    }

    /**
     * load api config and deploy
     * 
     * @return
     */
    private Future<Void> load() {
        return Future.future(p -> {
            p.complete();
        });
    }

    private Future<String> deploy(String verticleName, DeploymentOptions deployOptions) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(verticleName, deployOptions, promise);
        return promise.future();
    }

}
