/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
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

package com.dinstone.agate.gateway.deploy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.handler.OperationHandler;
import com.dinstone.agate.gateway.options.GatewayOptions;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.plugin.PluginOptions;
import com.dinstone.agate.gateway.plugin.RoutePlugin;

import io.vertx.core.Vertx;

public class RouteDeploy {

    private ApplicationContext appContext;

    private GatewayOptions gatewayOptions;

    private RouteOptions routeOptions;

    private RoutePlugin routingPlugin;

    private List<RoutePlugin> failurePlugins;

    private List<RoutePlugin> beforePlugins;

    private List<RoutePlugin> afterPlugins;

    public RouteDeploy(ApplicationContext appContext, GatewayOptions gatewayOptions, RouteOptions routeOptions) {
        this.appContext = appContext;
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

    public void destory() {
        synchronized (this) {
            if (routingPlugin != null) {
                routingPlugin.destory();
            }
            if (failurePlugins != null) {
                failurePlugins.stream().filter(rp -> rp != null).forEach(rp -> rp.destory());
            }
            if (beforePlugins != null) {
                beforePlugins.stream().filter(rp -> rp != null).forEach(rp -> rp.destory());
            }
            if (afterPlugins != null) {
                afterPlugins.stream().filter(rp -> rp != null).forEach(rp -> rp.destory());
            }
        }
    }

    private List<RoutePlugin> createPlugins(PluginOptions... plugins) {
        List<RoutePlugin> routePlugins;
        if (plugins != null) {
            routePlugins = Stream.of(plugins).sorted((p, q) -> p.getOrder() - q.getOrder())
                .map(pluginOptions -> appContext.createPlugin(routeOptions, pluginOptions)).filter(p -> p != null)
                .collect(Collectors.toList());

        } else {
            routePlugins = Collections.emptyList();
        }
        return routePlugins;
    }

    public List<OperationHandler> getBeforeHandlers(Vertx vertx) {
        synchronized (this) {
            if (beforePlugins == null) {
                beforePlugins = createPlugins(routeOptions.getBefores());
            }
        }
        if (beforePlugins != null) {
            return beforePlugins.stream().map(p -> p.createHandler(vertx)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<OperationHandler> getAfterHandlers(Vertx vertx) {
        synchronized (this) {
            if (afterPlugins == null) {
                afterPlugins = createPlugins(routeOptions.getAfters());
            }
        }
        if (afterPlugins != null) {
            return afterPlugins.stream().map(p -> p.createHandler(vertx)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<OperationHandler> getFailureHandlers(Vertx vertx) {
        synchronized (this) {
            if (failurePlugins == null) {
                failurePlugins = createPlugins(routeOptions.getFailures());
            }
        }
        if (failurePlugins != null) {
            return failurePlugins.stream().map(p -> p.createHandler(vertx)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public OperationHandler getRoutingHandler(Vertx vertx) {
        synchronized (this) {
            if (routingPlugin == null) {
                routingPlugin = createPlugins(routeOptions.getRouting()).get(0);
            }
        }
        return routingPlugin.createHandler(vertx);
    }

}
