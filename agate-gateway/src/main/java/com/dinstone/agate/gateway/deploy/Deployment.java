/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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

    private Map<String, AppDeploy> appDeployMap = new ConcurrentHashMap<>();

    private String cluster;

    public Deployment(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public AppDeploy get(String appName) {
        return appDeployMap.get(appName);
    }

    public void put(AppDeploy appDeploy) {
        appDeployMap.put(appDeploy.getAppName(), appDeploy);
    }

    public AppDeploy remove(String appName) {
        return appDeployMap.remove(appName);
    }

    public void destroy() {
        appDeployMap.clear();
    }

}
