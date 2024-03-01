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
package io.agate.gateway.plugin.internal;

import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.handler.internal.HttpProxyHandler;
import io.agate.gateway.options.RouteOptions;
import io.agate.gateway.plugin.PluginOptions;
import io.agate.gateway.plugin.RouteHandlerPlugin;
import io.agate.gateway.service.ConsulServiceAddressSupplier;
import io.agate.gateway.service.FixedServiceAddressSupplier;
import io.agate.gateway.service.Loadbalancer;
import io.agate.gateway.service.RoundRobinLoadBalancer;
import io.agate.gateway.service.ServiceAddressSupplier;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;

public class HttpProxyPlugin extends RouteHandlerPlugin {

	private HttpClient httpClient;

	private Loadbalancer loadBalancer;

	private ServiceAddressSupplier serviceSupplier;

	public HttpProxyPlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
		super(routeOptions, pluginOptions);
	}

	@Override
	public void destroy() {
		synchronized (this) {
			if (httpClient != null) {
				httpClient.close();
			}
			if (serviceSupplier != null) {
				serviceSupplier.close();
			}
		}
	}

	@Override
	public RouteHandler createHandler(Vertx vertx) {
		HttpClient httpClient = createHttpClient(vertx, routeOptions);
		Loadbalancer loadbalancer = createLoadbalancer(vertx, routeOptions);
		return new HttpProxyHandler(routeOptions, httpClient, loadbalancer);
	}

	public Loadbalancer createLoadbalancer(Vertx vertx, RouteOptions routeOptions) {
		synchronized (this) {
			if (loadBalancer == null) {
				// service discovery
				if (routeOptions.getBackend().getType() == 1) {
					serviceSupplier = new ConsulServiceAddressSupplier(vertx, routeOptions);
					loadBalancer = new RoundRobinLoadBalancer(routeOptions, serviceSupplier);
				} else {
					serviceSupplier = new FixedServiceAddressSupplier(vertx, routeOptions);
					loadBalancer = new RoundRobinLoadBalancer(routeOptions, serviceSupplier);
				}
			}

			return loadBalancer;
		}
	}

	public HttpClient createHttpClient(Vertx vertx, RouteOptions routeOptions) {
		synchronized (this) {
			if (httpClient == null) {
				HttpClientOptions clientOptions;
				JsonObject config = routeOptions.getBackend().getConnection();
				if (config != null) {
					clientOptions = new HttpClientOptions(config);
				} else {
					clientOptions = new HttpClientOptions();
					clientOptions.setKeepAlive(true);
					clientOptions.setConnectTimeout(2000);
					// clientOptions.setMaxWaitQueueSize(1000);
					clientOptions.setIdleTimeout(10);
					clientOptions.setMaxPoolSize(100);
					// clientOptions.setTracingPolicy(TracingPolicy.PROPAGATE);
				}
				httpClient = vertx.createHttpClient(clientOptions);
			}
			return httpClient;
		}
	}

}
