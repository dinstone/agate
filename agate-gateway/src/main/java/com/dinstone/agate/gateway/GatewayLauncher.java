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
package com.dinstone.agate.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.utils.ConfigUtil;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

public class GatewayLauncher extends Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayLauncher.class);

    private static final String DEFAULT_GATEWAY_CONFIG = "config.json";

    private JsonObject config;

    public static void main(String[] args) {
        // disable DnsResolver
        System.setProperty("vertx.disableDnsResolver", "true");

        // init vertx log factory
        if (System.getProperty("vertx.logger-delegate-factory-class-name") == null) {
            System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
        }

        new GatewayLauncher().dispatch(args);
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
        // cluster is disabled
        vertxOptions.getEventBusOptions().setClustered(false);

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

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        deploymentOptions.setConfig(config).setInstances(1);
    }

}