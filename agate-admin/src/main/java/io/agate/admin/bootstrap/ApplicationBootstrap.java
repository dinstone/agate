
package io.agate.admin.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.admin.WebServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;

public class ApplicationBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationBootstrap.class);

    private Vertx vertx;

    public void start() {
        VertxOptions options = new VertxOptions().setBlockedThreadCheckInterval(10000000).setEventLoopPoolSize(2)
            .setWorkerPoolSize(4).setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(false));
        vertx = Vertx.vertx(options);

        LOG.info("agate admin web server is starting");
        vertx.deployVerticle(WebServerVerticle.class, new DeploymentOptions().setInstances(2)).onComplete(ar -> {
            if (ar.succeeded()) {
                LOG.info("agate admin web server start success");
            } else {
                LOG.info("agate admin web server start failed", ar.cause());
            }
        });
    }

    public void stop() {
        if (vertx != null) {
            vertx.close().onComplete(v -> {
                LOG.info("agate admin stopped");
            });
        }
    }

}
