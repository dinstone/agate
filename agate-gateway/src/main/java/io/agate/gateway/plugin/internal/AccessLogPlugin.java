package io.agate.gateway.plugin.internal;

import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.handler.internal.AccessLogHandler;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.vertx.core.Vertx;

public class AccessLogPlugin extends RouteHandlerPlugin {

    @Override
    public RouteHandler createHandler(Vertx vertx, RouteOptions routeOptions, PluginOptions pluginOptions) {
        return new AccessLogHandler();
    }
}
