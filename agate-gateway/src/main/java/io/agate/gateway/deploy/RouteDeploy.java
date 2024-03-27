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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.agate.gateway.context.ApplicationContext;
import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.options.GatewayOptions;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginFactory;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.vertx.core.Vertx;

public class RouteDeploy {

    private final PluginFactory pluginFactory;

    private final GatewayOptions gatewayOptions;

    private final RouteOptions routeOptions;

    private List<RouteHandlerPlugin> plugins;

    public RouteDeploy(ApplicationContext appContext, GatewayOptions gatewayOptions, RouteOptions routeOptions) {
        this.pluginFactory = appContext.getPluginFactory();
        this.gatewayOptions = gatewayOptions;
        this.routeOptions = routeOptions;
    }

    public String getRoute() {
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
            if (plugins != null) {
                plugins.stream().filter(Objects::nonNull).forEach(RouteHandlerPlugin::destroy);
            }
        }
    }

    private List<RouteHandlerPlugin> createPlugins() {
        List<PluginOptions> pluginOpionsList = pluginFactory.getGlobalPlugins();
        if (routeOptions.getPlugins() != null) {
            pluginOpionsList.addAll(routeOptions.getPlugins());
        }
        return pluginOpionsList.stream().distinct()
                .map(pluginOptions -> pluginFactory.createPlugin(routeOptions, pluginOptions))
                .collect(Collectors.toList());
    }

    public List<RouteHandler> getRoutingHandlers(Vertx vertx) {
        return initPlugins().stream().filter(rp -> !rp.failure()).map(p -> p.createHandler(vertx)).sorted()
                .collect(Collectors.toList());
    }

    public List<RouteHandler> getFailureHandlers(Vertx vertx) {
        return initPlugins().stream().filter(rp -> rp.failure()).map(p -> p.createHandler(vertx)).sorted()
                .collect(Collectors.toList());
    }

    private List<RouteHandlerPlugin> initPlugins() {
        synchronized (this) {
            if (plugins == null) {
                plugins = createPlugins();
            }
        }
        return plugins;
    }

}
