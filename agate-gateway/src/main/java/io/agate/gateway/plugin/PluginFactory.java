/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.internal.HttpProxyPlugin;
import io.agate.gateway.plugin.internal.ProxyReplyPlugin;
import io.agate.gateway.plugin.internal.RestfulFailurePlugin;

public class PluginFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

	private final Map<String, Class<? extends RouteHandlerPlugin>> PLUGIN_MAP = new HashMap<>();

	private final List<PluginOptions> globalPlugins = new LinkedList<>();

	public PluginFactory() {
		registPlugin(HttpProxyPlugin.class);
		registPlugin(ProxyReplyPlugin.class);
		registPlugin(RestfulFailurePlugin.class);

		globalPlugins.add(new PluginOptions(HttpProxyPlugin.class.getSimpleName(), null));
		globalPlugins.add(new PluginOptions(ProxyReplyPlugin.class.getSimpleName(), null));
		globalPlugins.add(new PluginOptions(RestfulFailurePlugin.class.getSimpleName(), null));
	}

	private void registPlugin(Class<? extends RouteHandlerPlugin> pluginClass) {
		PLUGIN_MAP.put(pluginClass.getSimpleName(), pluginClass);
	}

	public List<PluginOptions> getGlobalPlugins() {
		return new ArrayList<>(globalPlugins);
	}

	public RouteHandlerPlugin createPlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
		try {
			Class<? extends RouteHandlerPlugin> pc = PLUGIN_MAP.get(pluginOptions.getPlugin());
			if (pc != null) {
				return pc.getConstructor(RouteOptions.class, PluginOptions.class).newInstance(routeOptions,
						pluginOptions);
			}
		} catch (Exception e) {
			LOG.warn("[{}.{}] plugin instance init error: {}", routeOptions.getRoute(), pluginOptions.getPlugin(), e);
			throw new RuntimeException("create plugin error", e);
		}
		throw new RuntimeException(
				"can't find plugin [" + pluginOptions.getPlugin() + "] for " + routeOptions.getRoute());
	}
}
