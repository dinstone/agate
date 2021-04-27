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
package com.dinstone.agate.gateway.deploy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Deployment {

    private Map<String, GatewayDeploy> deployMap = new ConcurrentHashMap<>();

    private String cluster;

    public Deployment(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public GatewayDeploy get(String appName) {
        return deployMap.get(appName);
    }

    public void put(GatewayDeploy appDeploy) {
        deployMap.put(appDeploy.getGateway(), appDeploy);
    }

    public GatewayDeploy remove(String appName) {
        return deployMap.remove(appName);
    }

    public void destroy() {
        deployMap.clear();
    }

}
