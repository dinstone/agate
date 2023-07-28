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

package io.agate.gateway.context;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.deploy.ClusterDeploy;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.agate.gateway.plugin.internal.HttpProxyPlugin;
import io.agate.gateway.plugin.internal.ProxyReplyPlugin;
import io.agate.gateway.plugin.internal.RestfulFailurePlugin;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;

public class ApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

    private final Map<String, Class<? extends RouteHandlerPlugin>> PLUGIN_MAP = new HashMap<>();

    private JsonObject config;

    private String clusterCode;

    private ClusterDeploy clusterDeploy;

    private ConsulClientOptions consulOptions;

    private ConsulClient consulClient;

    public ApplicationContext(JsonObject config) {
        this.config = config;

        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException("init application context error", e);
        }
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

        regist("HttpProxyPlugin", HttpProxyPlugin.class);
        regist("ProxyReplyPlugin", ProxyReplyPlugin.class);
        regist("RestfulFailurePlugin", RestfulFailurePlugin.class);

        LOG.debug("init application context ended");
    }

    private void regist(String pluginName, Class<? extends RouteHandlerPlugin> pluginClass) {
        PLUGIN_MAP.put(pluginName, pluginClass);
    }

    public RouteHandlerPlugin createPlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
        try {
            Class<? extends RouteHandlerPlugin> pc = PLUGIN_MAP.get(pluginOptions.getPlugin());
            if (pc != null) {
                return pc.getConstructor(RouteOptions.class, PluginOptions.class).newInstance(routeOptions,
                    pluginOptions);
            }
        } catch (Exception e) {
            LOG.warn("plugin instance error", e);
        }
        return null;
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

    public ConsulClient getConsulClient(Vertx vertx) {
        synchronized (this) {
            if (consulClient == null) {
                consulClient = ConsulClient.create(vertx, consulOptions);
            }
        }
        return consulClient;
    }

}
