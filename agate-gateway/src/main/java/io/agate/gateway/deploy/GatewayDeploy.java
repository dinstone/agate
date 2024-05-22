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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.agate.gateway.options.GatewayOptions;
import io.agate.gateway.verticle.GatewayVerticle;

/**
 * gateway deployment info.
 *
 * @author dinstone
 */
public class GatewayDeploy {

    private final Map<String, RouteDeploy> routeDeployCaches = new ConcurrentHashMap<>();

    private final List<GatewayVerticle> gatewayVerticles = new CopyOnWriteArrayList<>();

    private final GatewayOptions gatewayOptions;

    private String deployId;

    public GatewayDeploy(GatewayOptions gatewayOptions) {
        this.gatewayOptions = gatewayOptions;
    }

    public String getGatewayName() {
        return gatewayOptions.getGateway();
    }

    public GatewayOptions getGatewayOptions() {
        return gatewayOptions;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getDeployId() {
        return deployId;
    }

    public List<GatewayVerticle> getGatewayVerticles() {
        return gatewayVerticles;
    }

    public void registryVerticle(GatewayVerticle verticle) {
        gatewayVerticles.add(verticle);
    }

    public void removeVerticle(GatewayVerticle verticle) {
        gatewayVerticles.remove(verticle);
    }

    public boolean hasRoute(String route) {
        return routeDeployCaches.containsKey(route);
    }

    public RouteDeploy getRoute(String route) {
        return routeDeployCaches.get(route);
    }

    public void registryRoute(RouteDeploy routeDeploy) {
        routeDeployCaches.put(routeDeploy.getRoute(), routeDeploy);
    }

    public void removeRoute(RouteDeploy routeDeploy) {
        routeDeployCaches.remove(routeDeploy.getRoute());
    }

    public void destroy() {
        for (RouteDeploy deploy : routeDeployCaches.values()) {
            deploy.destroy();
        }
        routeDeployCaches.clear();

        gatewayVerticles.clear();
    }
}
