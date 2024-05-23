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
package io.agate.gateway.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.context.ApplicationContext;
import io.agate.gateway.plugin.internal.AccessLogPlugin;
import io.agate.gateway.plugin.internal.CircuitBreakerPlugin;
import io.agate.gateway.plugin.internal.HttpProxyPlugin;
import io.agate.gateway.plugin.internal.MeterMetricsPlugin;
import io.agate.gateway.plugin.internal.ProxyReplyPlugin;
import io.agate.gateway.plugin.internal.RateLimitPlugin;
import io.agate.gateway.plugin.internal.RestfulFailurePlugin;
import io.agate.gateway.plugin.internal.ZipkinTracingPlugin;

public class PluginManager {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

    private final Map<String, RouteHandlerPlugin> PLUGIN_MAP = new HashMap<>();

    private final List<PluginOptions> globalPlugins = new LinkedList<>();

    public PluginManager() {
        registerPlugin(new AccessLogPlugin());
        registerPlugin(new HttpProxyPlugin());
        registerPlugin(new ProxyReplyPlugin());
        registerPlugin(new RestfulFailurePlugin());

        registerPlugin(new CircuitBreakerPlugin());
        registerPlugin(new MeterMetricsPlugin());
        registerPlugin(new ZipkinTracingPlugin());
        registerPlugin(new RateLimitPlugin());

        LOG.info("{}", PLUGIN_MAP.values());

        globalPlugins.add(new PluginOptions(HttpProxyPlugin.class.getSimpleName(), null));
        globalPlugins.add(new PluginOptions(ProxyReplyPlugin.class.getSimpleName(), null));
        globalPlugins.add(new PluginOptions(RestfulFailurePlugin.class.getSimpleName(), null));
    }

    public void registerPlugin(RouteHandlerPlugin plugin) {
        PLUGIN_MAP.put(plugin.getClass().getSimpleName(), plugin);
    }

    public List<PluginOptions> getGlobalPlugins() {
        return new ArrayList<>(globalPlugins);
    }

    public RouteHandlerPlugin findPlugin(String pluginName) {
        return PLUGIN_MAP.get(pluginName);
    }
}
