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
package io.agate.gateway.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.deploy.ClusterDeploy;
import io.agate.gateway.plugin.PluginManager;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;

public class ApplicationContext {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

	private final JsonObject config;

	private String clusterCode;

	private ClusterDeploy clusterDeploy;

	private ConsulClientOptions consulOptions;

	private ConsulClient consulClient;

	private PluginManager pluginManager;

	public ApplicationContext(JsonObject config) {
		this.config = config;

		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException("init application context error", e);
		}
	}

	private void init() {
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

		pluginManager = new PluginManager();

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

	public ConsulClient getConsulClient(Vertx vertx) {
		synchronized (this) {
			if (consulClient == null) {
				consulClient = ConsulClient.create(vertx, consulOptions);
			}
		}
		return consulClient;
	}

	public PluginManager getPluginFactory() {
		return pluginManager;
	}

}
