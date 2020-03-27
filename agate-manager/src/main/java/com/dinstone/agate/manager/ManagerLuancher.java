package com.dinstone.agate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.manager.utils.ConfigUtil;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxJmxMetricsOptions;

public class ManagerLuancher extends Launcher {

	private static final Logger LOG = LoggerFactory.getLogger(ManagerLuancher.class);

	private static final String DEFAULT_GATEWAY_CONFIG = "config.json";

	private JsonObject config;

	public static void main(String[] args) {
		// disable DnsResolver
		System.setProperty("vertx.disableDnsResolver", "true");

		// init vertx log factory
		if (System.getProperty("vertx.logger-delegate-factory-class-name") == null) {
			System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		}

		new ManagerLuancher().dispatch(args);
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

	private MicrometerMetricsOptions metricsOptions() {
		VertxJmxMetricsOptions jmxo = new VertxJmxMetricsOptions().setEnabled(true);
		return new MicrometerMetricsOptions().setEnabled(true).setJvmMetricsEnabled(true).setJmxMetricsOptions(jmxo);
	}

	@Override
	public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
		deploymentOptions.setConfig(config).setInstances(1);
	}

}