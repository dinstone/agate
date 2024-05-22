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
package io.agate.gateway.deploy;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.ApplicationContext;
import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.options.GatewayOptions;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginManager;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.vertx.core.Vertx;

public class RouteDeploy {

    private static final Logger LOG = LoggerFactory.getLogger(RouteDeploy.class);

    private final PluginManager pluginManager;

    private final GatewayOptions gatewayOptions;

    private final RouteOptions routeOptions;

    private List<RouteHandler> routingHandlers;

    private List<RouteHandler> failureHandlers;

    public RouteDeploy(ApplicationContext appContext, GatewayOptions gatewayOptions, RouteOptions routeOptions) {
        this.pluginManager = appContext.getPluginFactory();
        this.gatewayOptions = gatewayOptions;
        this.routeOptions = routeOptions;
    }

    public String getRouteName() {
        return routeOptions.getRoute();
    }

    public RouteOptions getRouteOptions() {
        return routeOptions;
    }

    public GatewayOptions getGatewayOptions() {
        return gatewayOptions;
    }

    public void destroy() {
        synchronized (this) {
            routingHandlers.forEach(RouteHandler::destroy);
            failureHandlers.forEach(RouteHandler::destroy);
        }
    }

    public List<RouteHandler> getRoutingHandlers(Vertx vertx) {
        synchronized (this) {
            if (routingHandlers == null) {
                initHandlers(vertx);
            }
            return routingHandlers;
        }
    }

    public List<RouteHandler> getFailureHandlers(Vertx vertx) {
        synchronized (this) {
            if (failureHandlers == null) {
                initHandlers(vertx);
            }
            return failureHandlers;
        }
    }

    private void initHandlers(Vertx vertx) {
        List<PluginOptions> pluginOpionsList = pluginManager.getGlobalPlugins();
        if (routeOptions.getPlugins() != null) {
            pluginOpionsList.addAll(routeOptions.getPlugins());
        }

        List<RouteHandler> routings = new ArrayList<>(pluginOpionsList.size());
        List<RouteHandler> failures = new ArrayList<>(pluginOpionsList.size());
        for (PluginOptions pluginOptions : pluginOpionsList) {
            RouteHandlerPlugin plugin = pluginManager.findPlugin(pluginOptions.getPlugin());
            if (plugin == null) {
                LOG.warn("unknown Plugin[{}] for Route[{}]", pluginOptions.getPlugin(), routeOptions.getRoute());
                continue;
            }
            if (plugin.failure()) {
                failures.add(plugin.createHandler(vertx, routeOptions, pluginOptions));
            } else {
                routings.add(plugin.createHandler(vertx, routeOptions, pluginOptions));
            }
        }

        routings.sort(null);
        this.routingHandlers = routings;

        failures.sort(null);
        this.failureHandlers = failures;
    }

}
