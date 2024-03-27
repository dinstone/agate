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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterDeploy {

    private final Map<String, GatewayDeploy> deployMap = new ConcurrentHashMap<>();

    private final String cluster;

    public ClusterDeploy(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public GatewayDeploy get(String name) {
        return deployMap.get(name);
    }

    public void put(GatewayDeploy deploy) {
        deployMap.put(deploy.getGatewayName(), deploy);
    }

    public GatewayDeploy remove(String name) {
        GatewayDeploy deploy = deployMap.remove(name);
        if (deploy != null) {
            deploy.destroy();
        }
        return deploy;
    }

    public void destroy() {
        deployMap.clear();
    }

}
