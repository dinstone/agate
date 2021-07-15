/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dinstone.agate.gateway.options.GatewayOptions;
import com.dinstone.agate.gateway.verticle.GatewayVerticle;

/**
 * gateway deployment info.
 * 
 * @author dinstone
 *
 */
public class GatewayDeploy {

    private Map<String, ApiDeploy> apiDeployCaches = new ConcurrentHashMap<>();

    private List<GatewayVerticle> gatewayVerticles = new CopyOnWriteArrayList<>();

    private GatewayOptions gatewayOptions;

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

    public void regist(GatewayVerticle verticle) {
        gatewayVerticles.add(verticle);
    }

    public void remove(GatewayVerticle verticle) {
        gatewayVerticles.remove(verticle);
    }

    public boolean containApi(String apiName) {
        return apiDeployCaches.containsKey(apiName);
    }

    public ApiDeploy searchApi(String apiName) {
        return apiDeployCaches.get(apiName);
    }

    public void registApi(ApiDeploy apiDeploy) {
        apiDeployCaches.put(apiDeploy.getApiName(), apiDeploy);
    }

    public void removeApi(ApiDeploy apiDeploy) {
        apiDeploy.destory();
        apiDeployCaches.remove(apiDeploy.getApiName());
    }

    public void destroy() {
        for (ApiDeploy deploy : apiDeployCaches.values()) {
            deploy.destory();
        }
        apiDeployCaches.clear();

        gatewayVerticles.clear();
    }
}
