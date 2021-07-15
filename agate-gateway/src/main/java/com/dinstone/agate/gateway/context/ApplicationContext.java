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
package com.dinstone.agate.gateway.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.deploy.ClusterDeploy;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;

public class ApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

    private JsonObject config;

    private String clusterCode;

    private ClusterDeploy clusterDeploy;

    private ConsulClientOptions consulOptions;

    public ApplicationContext(JsonObject config) throws Exception {
        this.config = config;

        init();
    }

    private void init() throws Exception {
        LOG.debug("init application context start");

        // cluster id

        JsonObject node = config.getJsonObject("gateway");
        if (node == null) {
            throw new IllegalArgumentException("gateway is empty");
        }
        String cluster = node.getString("cluster");
        if (cluster == null || cluster.isEmpty()) {
            throw new IllegalArgumentException("cluster is empty");
        }
        clusterCode = cluster;

        // consul options
        JsonObject consulJson = config.getJsonObject("consul");
        if (consulJson != null) {
            consulOptions = new ConsulClientOptions(consulJson);
        } else {
            consulOptions = new ConsulClientOptions();
        }

        // deployment
        clusterDeploy = new ClusterDeploy(clusterCode);

        LOG.debug("init application context ended");
    }

    public void destroy() {
        clusterDeploy.destroy();
    }

    public JsonObject getConfig() {
        return config;
    }

    public ClusterDeploy getClusterDeploy() {
        return clusterDeploy;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public ConsulClientOptions getConsulOptions() {
        return consulOptions;
    }

}
