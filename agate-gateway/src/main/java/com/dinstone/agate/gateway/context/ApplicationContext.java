/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.deploy.Deployment;
import com.dinstone.agate.tracing.ZipkinTracer;

import brave.Tracing;
import brave.sampler.Sampler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.tracing.zipkin.HttpSender;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;
import zipkin2.reporter.AsyncReporter;

public class ApplicationContext {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);

	private static final String DEFAULT_CLUSTER = "default";

	private JsonObject config;

	private String clusterId;

	private Deployment deployment;

	private ZipkinTracer zipkinTracer;

	private ConsulClientOptions consulOptions;

	public ApplicationContext(JsonObject config) throws Exception {
		this.config = config;

		init();
	}

	private void init() throws Exception {
		LOG.debug("init application context start");

		// cluster id
		clusterId = config.getString("cluster", DEFAULT_CLUSTER);

		// consul options
		JsonObject consulJson = config.getJsonObject("consul");
		if (consulJson != null) {
			consulOptions = new ConsulClientOptions(consulJson);
		} else {
			consulOptions = new ConsulClientOptions();
		}

		Tracing tracing = Tracing.current();
		if (tracing == null) {
			JsonObject tracingJson = config.getJsonObject("tracing");
			ZipkinTracingOptions zipkinOptions = tracingOptions(tracingJson);
			HttpSender sender = new HttpSender(zipkinOptions.getSenderOptions());
			tracing = Tracing.newBuilder().localServiceName(zipkinOptions.getServiceName())
					.spanReporter(AsyncReporter.builder(sender).build())
					.sampler(Sampler.create(zipkinOptions.getProbability())).build();
			zipkinTracer = new ZipkinTracer(tracing) {
				@Override
				public void destroy() {
					super.destroy();
					try {
						sender.close();
					} catch (IOException e) {
						// ignore
					}
				}
			};
		} else {
			zipkinTracer = new ZipkinTracer(tracing);
		}

		// deployment
		deployment = new Deployment(clusterId);

		LOG.debug("init application context ended");
	}

	private ZipkinTracingOptions tracingOptions(JsonObject tracingConfig) {
		ZipkinTracingOptions options;
		if (tracingConfig != null) {
			options = new ZipkinTracingOptions(tracingConfig);
		} else {
			options = new ZipkinTracingOptions("agate-gateway");
		}
		if (options.getServiceName() == null || options.getServiceName().isEmpty()) {
			options.setServiceName("agate-gateway");
		}
		return options;
	}

	public void destroy() {
		deployment.destroy();
		zipkinTracer.destroy();
	}

	public JsonObject getConfig() {
		return config;
	}

	public Deployment getDeployment() {
		return deployment;
	}

	public String getClusterId() {
		return clusterId;
	}

	public ZipkinTracer getZipkinTracer() {
		return zipkinTracer;
	}

	public ConsulClientOptions getConsulOptions() {
		return consulOptions;
	}

}
