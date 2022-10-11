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

package com.dinstone.agate.manager.model;

import java.util.List;

public class RouteConfig {

    private Integer arId;

    private Integer gwId;

    private String name;

    private String remark;

    private String gateway;

    private int status;

    private RequestConfig requestConfig;

    private RoutingConfig routingConfig;

    private ResponseConfig responseConfig;

    private List<PluginConfig> pluginConfigs;

    public Integer getArId() {
        return arId;
    }

    public void setArId(Integer arId) {
        this.arId = arId;
    }

    public Integer getGwId() {
        return gwId;
    }

    public void setGwId(Integer gwId) {
        this.gwId = gwId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public RoutingConfig getRoutingConfig() {
        return routingConfig;
    }

    public void setRoutingConfig(RoutingConfig routingConfig) {
        this.routingConfig = routingConfig;
    }

    public ResponseConfig getResponseConfig() {
        return responseConfig;
    }

    public void setResponseConfig(ResponseConfig responseConfig) {
        this.responseConfig = responseConfig;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public List<PluginConfig> getPluginConfigs() {
        return pluginConfigs;
    }

    public void setPluginConfigs(List<PluginConfig> pluginConfigs) {
        this.pluginConfigs = pluginConfigs;
    }

}
