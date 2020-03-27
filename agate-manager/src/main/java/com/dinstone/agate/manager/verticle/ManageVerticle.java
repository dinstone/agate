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

import java.net.InetAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.manager.context.ApplicationContext;
import com.dinstone.agate.manager.handler.AccessLogHandler;
import com.dinstone.agate.manager.handler.AuthenApiHandler;
import com.dinstone.agate.manager.utils.NetworkUtil;
import com.dinstone.vertx.web.RouterBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * the web endpoint of managing APP and API deployment.
 * 
 * @author dinstone
 *
 */
public class ManageVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(ManageVerticle.class);

	private static final int DEFAULT_PORT = 5555;

	private ApplicationContext appContext;

	private HttpServerOptions serverOptions;

	public ManageVerticle(ApplicationContext context) {
		this.appContext = context;
	}

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);

		// init server options
		serverOptions = new HttpServerOptions().setIdleTimeout(180);
		JsonObject mconfig = config().getJsonObject("manage");
		if (mconfig == null) {
			mconfig = new JsonObject();
		}
		try {
			if (mconfig.getString("host") == null) {
				List<InetAddress> pas = NetworkUtil.getPrivateAddresses();
				if (pas != null && pas.size() > 0) {
					serverOptions.setHost(pas.get(0).getHostAddress());
				}
			} else {
				serverOptions.setHost(mconfig.getString("host"));
			}
		} catch (Exception e) {
			LOG.warn("unkown host, use default ip 0.0.0.0", e);
		}
		serverOptions.setPort(mconfig.getInteger("port", DEFAULT_PORT));
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.createHttpServer(serverOptions).requestHandler(createRouter()).listen(ar -> {
			if (ar.succeeded()) {
				LOG.info("manage verticle start success, {}:{}", serverOptions.getHost(), serverOptions.getPort());
				startPromise.complete();
			} else {
				LOG.error("manage verticle start failed, {}:{}", serverOptions.getHost(), serverOptions.getPort());
				startPromise.fail(ar.cause());
			}
		});
	}

	@Override
	public void stop(Promise<Void> stopPromise) throws Exception {
	}

	private Router createRouter() {
		Router mainRouter = Router.router(vertx);
		mainRouter.route().failureHandler(failureHandler()).handler(new AccessLogHandler()).handler(sessionHandler());

		// health check
		mainRouter.route("/health").handler(rc -> {
			rc.end("OK");
		});

		// api route
		RouterBuilder routerBuilder = RouterBuilder.create(vertx).handler(checkApiHandler())
				.handler(new AuthenApiHandler(appContext));
		mainRouter.mountSubRouter("/api", routerBuilder.build());

		// static file route
		mainRouter.route("/*").handler(checkHtmlHandler()).handler(StaticHandler.create().setCachingEnabled(false));

		return mainRouter;
	}

	private SessionHandler sessionHandler() {
		return SessionHandler.create(LocalSessionStore.create(vertx, LocalSessionStore.DEFAULT_SESSION_MAP_NAME, 60000))
				.setNagHttps(false);
	}

	private Handler<RoutingContext> checkHtmlHandler() {
		return rc -> {
			String path = rc.request().path();
			// index page
			if ("/".equals(path)) {
				rc.reroute("/index.html");
				return;
			}

			// skip non *.html
			if (!path.endsWith(".html")) {
				rc.next();
				return;
			}

			// ignore login.html
			String loginPage = "/login.html";
			if (loginPage.equals(path)) {
				rc.next();
				return;
			}

			// check authen
			if (rc.session().get("user") == null) {
				rc.response().putHeader(HttpHeaders.LOCATION, loginPage).setStatusCode(302)
						.end("Redirecting to " + loginPage + ".");
			} else {
				rc.next();
			}
		};
	}

	private Handler<RoutingContext> checkApiHandler() {
		return rc -> {
			// igonre
			String path = rc.request().path();
			if (path.contains("login") || path.contains("logout")) {
				rc.next();
				return;
			}

			// check
			if (rc.session().get("user") == null) {
				rc.response().setStatusCode(401).end();
			} else {
				rc.next();
			}
		};
	}

	private Handler<RoutingContext> failureHandler() {
		return rc -> {
			LOG.error("failure handle for {}, {}:{}", rc.request().path(), rc.statusCode(), rc.failure());
			int statusCode = rc.statusCode();
			if (statusCode == -1) {
				statusCode = 500;
			}
			if (rc.failure() != null) {
				rc.response().setStatusCode(statusCode).end(rc.failure().getMessage());
			} else {
				rc.response().setStatusCode(statusCode).end();
			}
		};
	}

}
