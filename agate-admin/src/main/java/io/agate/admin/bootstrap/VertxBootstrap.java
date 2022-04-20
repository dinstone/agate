
package io.agate.admin.bootstrap;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;

@Component
public class VertxBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(VertxBootstrap.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Vertx vertx;

    // public VertxBootstrap(ApplicationContext applicationContext) {
    // super();
    // this.applicationContext = applicationContext;
    // }

    public void start() throws Exception {
        VertxOptions options = new VertxOptions().setBlockedThreadCheckInterval(10000000).setEventLoopPoolSize(2)
            .setWorkerPoolSize(4).setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(false));
        vertx = Vertx.vertx(options);

        vertx.registerVerticleFactory(new SpringVerticleFactory(applicationContext));

        deployWebServerVerticle();
    }

    public void stop() {
        if (vertx != null) {
            vertx.close().onComplete(v -> {
                LOG.info("agate admin web server stopped");
            });
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
