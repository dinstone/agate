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
package com.dinstone.agate.manager.verticle;

import com.dinstone.agate.manager.context.AgateVerticleFactory;
import com.dinstone.agate.manager.context.ApplicationContext;

import io.vertx.core.AbstractVerticle;
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

	private ApplicationContext appContext;

	@Override
	public void stop() throws Exception {
		if (appContext != null) {
			appContext.destroy();
		}
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		// init application context
		appContext = new ApplicationContext(config());

		// regist verticle factory
		vertx.registerVerticleFactory(new AgateVerticleFactory(appContext));

		// system verticle
		DeploymentOptions mvdOptions = new DeploymentOptions().setConfig(config()).setInstances(1);
		deploy(AgateVerticleFactory.appendPrefix(ManageVerticle.class), mvdOptions).onComplete(ar -> {
			if (ar.succeeded()) {
				startPromise.complete();
			} else {
				startPromise.fail(ar.cause());
			}
		});
	}

	private Future<String> deploy(String verticleName, DeploymentOptions deployOptions) {
		return Future.future(promise -> {
			vertx.deployVerticle(verticleName, deployOptions, promise);
		});
	}

}
