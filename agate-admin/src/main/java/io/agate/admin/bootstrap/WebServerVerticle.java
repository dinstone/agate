
package io.agate.admin.bootstrap;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dinstone.vertx.web.RouterBuilder;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.agate.admin.resource.AuthenResource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@Component
@Scope("prototype")
public class WebServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(WebServerVerticle.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServerOptions options = new HttpServerOptions().setPort(7777);
        vertx.createHttpServer(options).requestHandler(createHttpRouter()).listen(ar -> {
            if (ar.succeeded()) {
                LOG.info("web server verticle start success, listen on {}", ar.result().actualPort());
                startPromise.complete();
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }

    private Router createHttpRouter() {
        Router mainRouter = Router.router(vertx);
        // error handler
        mainRouter.errorHandler(401, rc -> {
            rc.response().setStatusCode(401).sendFile("webroot/401.html");
        });
        mainRouter.errorHandler(404, rc -> {
            rc.response().setStatusCode(404).sendFile("webroot/404.html");
        });

        mainRouter.route().handler(LoggerHandler.create());
        // allRoute.handler(ResponseTimeHandler.create());

        RouterBuilder uacRouterBuilder = RouterBuilder.create(vertx);
        RouterBuilder apiRouterBuilder = RouterBuilder.create(vertx);
        Map<String, Object> bmaps = applicationContext.getBeansWithAnnotation(WebHandler.class);
        bmaps.forEach((k, v) -> {
            if (v instanceof AuthenResource) {
                uacRouterBuilder.handler(v);
            } else {
                apiRouterBuilder.handler(v);
            }
        });

        // session handler
        SessionHandler sessionHandler = createSessionHandler();
        // check authen handler
        CheckAuthenHandler checkHandler = CheckAuthenHandler.create();
        // user access control
        mainRouter.route("/uac/*").handler(sessionHandler);
        mainRouter.mountSubRouter("/uac/", uacRouterBuilder.build());
        // api request handler
        mainRouter.route("/api/*").handler(sessionHandler).handler(checkHandler);
        mainRouter.mountSubRouter("/api/", apiRouterBuilder.build());

        // view handler
        mainRouter.route("/view/*").handler(sessionHandler).handler(checkHandler);

        // static handler
        mainRouter.route().handler(StaticHandler.create().setCachingEnabled(true).setMaxAgeSeconds(300));

        return mainRouter;
    }

    private SessionHandler createSessionHandler() {
        SessionHandler sessionHandler = SessionHandler
            .create(LocalSessionStore.create(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, 3600000))
            .setSessionTimeout(60000).setNagHttps(false);
        return sessionHandler;
    }

}
