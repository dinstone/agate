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
package io.agate.admin.bootstrap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;

public class ApplicationBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationBootstrap.class);

    private ApplicationContext applicationContext;

    private Vertx vertx;

    public ApplicationBootstrap(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() throws Exception {
        VertxOptions options = new VertxOptions().setEventLoopPoolSize(4)
            .setWorkerPoolSize(4).setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(false));
        vertx = Vertx.vertx(options);

        vertx.registerVerticleFactory(new SpringVerticleFactory(applicationContext));

        deployWebServerVerticle();
    }

    public void stop() {
        if (vertx != null) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            vertx.close().onComplete(ar -> {
                LOG.info("agate admin web server stopped");
                if (ar.succeeded()) {
                    future.complete(true);
                } else {
                    future.complete(false);
                }
            });
            
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
            }
        }
    }

    private void deployWebServerVerticle() throws Exception {
        LOG.info("agate admin web server is starting");
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        vertx.deployVerticle(SpringVerticleFactory.verticleName(WebServerVerticle.class),
            new DeploymentOptions().setInstances(2)).onComplete(ar -> {
                if (ar.succeeded()) {
                    LOG.info("agate admin web server start success");
                    future.complete(true);
                } else {
                    LOG.error("agate admin web server start failed", ar.cause());
                    future.completeExceptionally(ar.cause());
                }
            });
        future.get();
    }

}
