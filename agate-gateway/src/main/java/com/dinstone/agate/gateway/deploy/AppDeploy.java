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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * APP deployment info.
 * 
 * @author dinstone
 *
 */
public class AppDeploy {

    private Map<String, ApiDeploy> apiDeployMap = new ConcurrentHashMap<>();

    private List<Deployer> apiDeployers = new CopyOnWriteArrayList<>();

    private String deployId;

    private String appName;

    public AppDeploy(String appName) {
        super();
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getDeployId() {
        return deployId;
    }

    public List<Deployer> getApiDeployers() {
        return apiDeployers;
    }

    public void clear() {
        apiDeployers.clear();
        apiDeployMap.clear();
    }

    public void regist(Deployer deployer) {
        apiDeployers.add(deployer);
    }

    public void remove(Deployer deployer) {
        apiDeployers.remove(deployer);
    }

    public boolean containApi(String apiName) {
        return apiDeployMap.containsKey(apiName);
    }

    public void registApi(ApiDeploy apiDeploy) {
        apiDeployMap.put(apiDeploy.getName(), apiDeploy);
    }

    public void removeApi(String apiName) {
        apiDeployMap.remove(apiName);
    }

}
