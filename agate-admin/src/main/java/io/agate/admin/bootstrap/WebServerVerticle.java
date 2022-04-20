
package io.agate.admin.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@Component
@Scope("prototype")
public class WebServerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(WebServerVerticle.class);

    private AuthenticationProvider authProvider;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        authProvider = new AuthenticationProvider() {

            @Override
            public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
                String username = credentials.getString("username");
                String password = credentials.getString("password");
                if ("agate".equals(username) && "123456".equals(password)) {
                    // get result
                    User user = User.fromName(username);
                    resultHandler.handle(Future.succeededFuture(user));
                } else {
                    resultHandler.handle(Future.failedFuture("username or password incorrect"));
                }
            }
        };

        HttpServerOptions options = new HttpServerOptions().setPort(7777);
        vertx.createHttpServer(options).requestHandler(createHttpServerRouter()).listen(ar -> {
            if (ar.succeeded()) {
                LOG.info("web server verticle start success, listen on {}", ar.result().actualPort());
                startPromise.complete();
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }

    private Router createHttpServerRouter() {
        Router mainRouter = Router.router(vertx);
        // error handler
        mainRouter.errorHandler(404, rc -> {
            rc.response().setStatusCode(404).sendFile("webroot/404.html");
        });
        // mainRouter.errorHandler(405, rc -> {
        // RestfulUtil.exception(rc, rc.statusCode(), "don’t match the HTTP Method " + rc.request().method());
        // });
        // mainRouter.errorHandler(406, rc -> {
        // RestfulUtil.exception(rc, rc.statusCode(),
        // "can’t provide a response with a content type matching Accept header");
        // });
        // mainRouter.errorHandler(415, rc -> {
        // RestfulUtil.exception(rc, rc.statusCode(), "can’t accept the Content-type");
        // });

        Route allRoute = mainRouter.route();
        allRoute.handler(LoggerHandler.create());
        allRoute.handler(BodyHandler.create());
        // allRoute.handler(ResponseTimeHandler.create());

        // session handler
        SessionHandler sessionHandler = createSessionHandler();
        // authen handler
        RedirectAuthHandler authenHandler = createAuthHandler();

        // user authentication handler
        mainRouter.route("/login").handler(sessionHandler).handler(this::login);
        mainRouter.route("/logout").handler(sessionHandler).handler(this::logout);

        // api handler
        mainRouter.route("/api/*").handler(sessionHandler).handler(authenHandler);
        // view handler
        mainRouter.route("/view/*").handler(sessionHandler).handler(authenHandler);

        mainRouter.route("/api/account").handler(this::account);

        // static handler
        mainRouter.route().handler(StaticHandler.create().setCachingEnabled(false).setIndexPage("index.html"));

        return mainRouter;
    }

    private RedirectAuthHandler createAuthHandler() {
        RedirectAuthHandler authHandler = RedirectAuthHandler.create(null, "/302.html");
        return authHandler;
    }

    private SessionHandler createSessionHandler() {
        SessionHandler sessionHandler = SessionHandler
            .create(LocalSessionStore.create(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, 60000))
            .setSessionTimeout(60000).setNagHttps(false);
        return sessionHandler;
    }

    private void account(RoutingContext rc) {
        // result json
        JsonObject json = new JsonObject();
        // set code
        json.put("code", 1);
        try {
            User user = rc.user();
            json.put("result", user.principal());
            writeJsonResponse(rc, json);
        } catch (Exception e) {
            json.put("code", -1);
            json.put("cause", e.getMessage());
            writeJsonResponse(rc, json);
        }

    }

    private void login(RoutingContext rc) {
        // result json
        JsonObject json = new JsonObject();
        // set code
        json.put("code", 1);

        try {
            // get request body
            JsonObject authInfo = rc.getBodyAsJson();
            authProvider.authenticate(authInfo, ar -> {
                if (ar.succeeded()) {
                    rc.setUser(ar.result());
                    // get session
                    Session session = rc.session();
                    if (session != null) {
                        // regenerate id
                        session.regenerateId();
                        // session account
                        session.put("LoginAccount", ar.result());
                    }
                    json.put("result", true);
                } else {
                    json.put("result", false);
                }

                writeJsonResponse(rc, json);
            });

        } catch (Exception e) {
            json.put("result", false);
            json.put("cause", e.getMessage());

            writeJsonResponse(rc, json);
        }
    }

    private void logout(RoutingContext rc) {
        // remove session account
        rc.session().remove("LoginAccount");
        // clear session user
        rc.clearUser();
        // go to the login page
        rc.response().putHeader("location", "/").setStatusCode(302).end();
    }

    private void writeJsonResponse(RoutingContext rc, JsonObject json) {
        HttpServerResponse response = rc.response();
        response.putHeader("Content-Type", "application/json");
        response.end(json.toBuffer());
    }

}
