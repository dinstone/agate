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
package com.dinstone.agate.gateway.utils;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;

public final class VertxUtil {

	private static final Logger LOG = LoggerFactory.getLogger(VertxUtil.class);

	public static Vertx createVertx(VertxOptions vertxOptions) throws Exception {
		CompletableFuture<Vertx> future = new CompletableFuture<>();
		if (vertxOptions.getEventBusOptions() != null) {
			Vertx.clusteredVertx(vertxOptions, asyncResult -> {
				if (asyncResult.succeeded()) {
					future.complete(asyncResult.result());
				} else {
					future.completeExceptionally(asyncResult.cause());
				}
			});
		} else {
			future.complete(Vertx.vertx(vertxOptions));
		}

		return future.get();
	}

	public static String deployVerticle(Vertx vertx, DeploymentOptions deployOptions, Verticle verticle)
			throws Exception {
		CompletableFuture<String> future = new CompletableFuture<>();
		vertx.deployVerticle(verticle, deployOptions, deployResponse -> {
			if (deployResponse.succeeded()) {
				LOG.info("verticle deployment is succeeded, {}:{}", verticle.getClass().getName(),
						deployResponse.result());
				future.complete(deployResponse.result());
			} else {
				LOG.error("verticle deployment is failed, {}:{}", verticle.getClass().getName(),
						deployResponse.cause());
				future.completeExceptionally(deployResponse.cause());
			}
		});

		return future.get();
	}

	public static String deployVerticle(Vertx vertx, DeploymentOptions deployOptions, String verticleName)
			throws Exception {
		return deployVerticle(vertx, deployOptions, null, verticleName);
	}

	public static String deployVerticle(Vertx vertx, DeploymentOptions deployOptions, VerticleFactory factory,
			String verticleName) throws Exception {
		Set<VerticleFactory> factories = vertx.verticleFactories();
		if (factory != null && !factories.contains(factory)) {
			vertx.registerVerticleFactory(factory);
		}

		CompletableFuture<String> future = new CompletableFuture<>();
		vertx.deployVerticle(verticleName, deployOptions, deployResponse -> {
			if (deployResponse.succeeded()) {
				LOG.info("verticle deployment is succeeded, {}:{}", verticleName, deployResponse.result());
				future.complete(deployResponse.result());
			} else {
				LOG.error("verticle deployment is failed, {}:{}", verticleName, deployResponse.cause());
				future.completeExceptionally(deployResponse.cause());
			}
		});

		return future.get();
	}

}
