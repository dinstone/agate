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

package com.dinstone.agate.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.utils.ConfigUtil;
import com.dinstone.agate.gateway.verticle.LaunchVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.tracing.TracingOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxJmxMetricsOptions;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;

public class AgateGatewayLauncher extends Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(AgateGatewayLauncher.class);

    private static final String DEFAULT_GATEWAY_CONFIG = "config.json";

    private JsonObject config;

    public static void main(String[] args) {
        // disable DnsResolver
        System.setProperty("vertx.disableDnsResolver", "true");

        // init vertx log factory
        if (System.getProperty("vertx.logger-delegate-factory-class-name") == null) {
            System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
        }

        if (args == null || args.length == 0) {
            args = new String[] { "run", LaunchVerticle.class.getName() };
        }
        new AgateGatewayLauncher().dispatch(args);
    }

    @Override
    public void afterConfigParsed(JsonObject config) {
        // config from command line
        if (!config.isEmpty()) {
            this.config = config;
        } else {
            this.config = ConfigUtil.loadConfig(DEFAULT_GATEWAY_CONFIG);
        }
        LOG.info("application config :\r\n{}", this.config.encodePrettily());
    }

    @Override
    public void beforeStartingVertx(VertxOptions vertxOptions) {
        // tracing
        vertxOptions.setTracingOptions(tracingOptions());

        // metrics
        vertxOptions.setMetricsOptions(metricsOptions());

        // native transport
        vertxOptions.setPreferNativeTransport(true);

        JsonObject vertxConfig = config.getJsonObject("vertx", new JsonObject());
        int blockedThreadCheckInterval = vertxConfig.getInteger("blockedThreadCheckInterval", -1);
        if (blockedThreadCheckInterval > 0) {
            vertxOptions.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
        }

        int eventLoopPoolSize = vertxConfig.getInteger("eventLoopPoolSize", -1);
        if (eventLoopPoolSize > 0) {
            vertxOptions.setEventLoopPoolSize(eventLoopPoolSize);
        }

        int workerPoolSize = vertxConfig.getInteger("workerPoolSize", -1);
        if (workerPoolSize > 0) {
            vertxOptions.setWorkerPoolSize(workerPoolSize);
        }
    }

    private TracingOptions tracingOptions() {
        ZipkinTracingOptions options;
        JsonObject tracingConfig = config.getJsonObject("tracing");
        if (tracingConfig != null) {
            options = new ZipkinTracingOptions(tracingConfig);
        } else {
            options = new ZipkinTracingOptions().setServiceName("agate-gateway");
        }
        if (options.getServiceName() == null || options.getServiceName().isEmpty()) {
            options.setServiceName("agate-gateway");
        }
        return options;
    }

    private MicrometerMetricsOptions metricsOptions() {
        VertxJmxMetricsOptions jmxo = new VertxJmxMetricsOptions().setEnabled(true);
        return new MicrometerMetricsOptions().setEnabled(true).setJvmMetricsEnabled(true).setJmxMetricsOptions(jmxo);
    }

    public void afterStartingVertx(Vertx vertx) {
        vertx.exceptionHandler(t -> {
            LOG.warn("default exception handler", t);
        });
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        deploymentOptions.setConfig(config).setInstances(1);
    }

}
