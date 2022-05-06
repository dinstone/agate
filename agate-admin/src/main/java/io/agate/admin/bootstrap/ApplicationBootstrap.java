
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
        VertxOptions options = new VertxOptions().setBlockedThreadCheckInterval(10000000).setEventLoopPoolSize(2)
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
