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
package com.dinstone.agate.gateway.verticle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.AddressConstant;
import com.dinstone.agate.gateway.context.AgateVerticleFactory;
import com.dinstone.agate.gateway.context.ApplicationContext;
import com.dinstone.agate.gateway.deploy.ClusterDeploy;
import com.dinstone.agate.gateway.deploy.GatewayDeploy;
import com.dinstone.agate.gateway.deploy.RouteDeploy;
import com.dinstone.agate.gateway.options.GatewayOptions;
import com.dinstone.agate.gateway.options.RouteOptions;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.KeyValue;
import io.vertx.ext.consul.KeyValueList;
import io.vertx.ext.consul.Watch;
import io.vertx.ext.consul.WatchResult;

/**
 * execute the deploy command of Gateway and Route.
 * 
 * @author dinstone
 *
 */
public class DeployVerticle extends AbstractVerticle {
	private static final Logger LOG = LoggerFactory.getLogger(DeployVerticle.class);

	private Map<String, Watch<KeyValueList>> routeWatchMap = new ConcurrentHashMap<>();

	private String clusterCode;

	private ClusterDeploy clusterDeploy;

	private ConsulClientOptions consulClientOptions;

	public DeployVerticle(ApplicationContext appContext) {
		this.clusterCode = appContext.getClusterCode();
		this.clusterDeploy = appContext.getClusterDeploy();
		this.consulClientOptions = new ConsulClientOptions(appContext.getConsulOptions()).setTimeout(0);
	}

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.eventBus().consumer(AddressConstant.GATEWAY_START, this::startGateway);
		vertx.eventBus().consumer(AddressConstant.GATEWAY_CLOSE, this::closeGateway);
		vertx.eventBus().consumer(AddressConstant.ROUTE_DEPLOY, this::deployRoute);
		vertx.eventBus().consumer(AddressConstant.ROUTE_REMOVE, this::removeRoute);

		startPromise.complete();
	}

	private void startGateway(Message<JsonObject> message) {
		GatewayOptions gatewayOptions = new GatewayOptions(message.body());
		String error = checkGatewayParams(gatewayOptions);
		if (error != null) {
			message.fail(400, error);
			return;
		}

		String gatewayName = gatewayOptions.getGateway();
		// check gateway is deploy
		if (clusterDeploy.get(gatewayName) != null) {
			message.reply(null);
			return;
		}
		clusterDeploy.put(new GatewayDeploy(gatewayOptions));

		// deploy gateway
		int instances = config().getInteger("instances", Runtime.getRuntime().availableProcessors());
		DeploymentOptions options = new DeploymentOptions().setConfig(message.body()).setInstances(instances);
		vertx.deployVerticle(AgateVerticleFactory.verticleName(GatewayVerticle.class), options, ar -> {
			if (ar.succeeded()) {
				clusterDeploy.get(gatewayName).setDeployId(ar.result());
				// watch gateway's route for deploying
				registRouteWatch(gatewayName);

				message.reply(null);
				LOG.info("start gateway success : {}", gatewayName);
			} else {
				LOG.warn("start gateway failure : {}, casue {}", gatewayName, ar.cause());

				clusterDeploy.remove(gatewayName);
				message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
			}
		});
	}

	private void closeGateway(Message<JsonObject> message) {
		GatewayOptions gatewayOptions = new GatewayOptions(message.body());
		String error = checkGatewayParams(gatewayOptions);
		if (error != null) {
			message.fail(400, error);
			return;
		}

		String gatewayName = gatewayOptions.getGateway();
		GatewayDeploy gatewayDeploy = clusterDeploy.get(gatewayName);
		if (gatewayDeploy == null || gatewayDeploy.getDeployId() == null) {
			message.reply(null);
			return;
		}

		vertx.undeploy(gatewayDeploy.getDeployId(), ar -> {
			if (ar.succeeded()) {
				clusterDeploy.remove(gatewayName);
				removeRouteWatch(gatewayName);
				message.reply(null);
				LOG.info("close gateway success : {}", gatewayName);
			} else {
				LOG.warn("close gateway failure : {}, casue {}", gatewayName, ar.cause());
				message.fail(500, ar.cause() == null ? "" : ar.cause().getMessage());
			}
		});
	}

	private void registRouteWatch(String gatewayName) {
		Watch<KeyValueList> routeWatch = Watch.keyPrefix("agate/route/" + gatewayName, vertx, consulClientOptions);
		routeWatch.setHandler(ar -> {
			try {
				watchEventHandle(ar);
			} catch (Exception e) {
				LOG.warn("handle route watch event error", e);
			}
		}).start();
		routeWatchMap.put(gatewayName, routeWatch);
	}

	private void watchEventHandle(WatchResult<KeyValueList> wr) {
		if (!wr.succeeded()) {
			LOG.warn("route watch event error", wr.cause());
			return;
		}

		Map<String, KeyValue> pkvMap = new HashMap<String, KeyValue>();
		if (wr.prevResult() != null && wr.prevResult().getList() != null) {
			wr.prevResult().getList().forEach(kv -> pkvMap.put(kv.getKey(), kv));
		}

		Map<String, KeyValue> nkvMap = new HashMap<String, KeyValue>();
		if (wr.nextResult() != null && wr.nextResult().getList() != null) {
			wr.nextResult().getList().forEach(kv -> nkvMap.put(kv.getKey(), kv));
		}

		// create: next have and prev not;
		// update: next have and prev have, modify index not equal
		List<KeyValue> cList = new LinkedList<KeyValue>();
		List<KeyValue> uList = new LinkedList<KeyValue>();
		nkvMap.forEach((k, nkv) -> {
			KeyValue pkv = pkvMap.get(k);
			if (pkv == null) {
				cList.add(nkv);
			} else if (pkv.getModifyIndex() != nkv.getModifyIndex()) {
				uList.add(nkv);
			}
		});

		// delete: prev have and next not;
		List<KeyValue> dList = new LinkedList<KeyValue>();
		pkvMap.forEach((k, pkv) -> {
			if (!nkvMap.containsKey(k)) {
				dList.add(pkv);
			}
		});

		uList.forEach(kv -> {
			try {
				JsonObject message = new JsonObject(kv.getValue());
				vertx.eventBus().request(AddressConstant.ROUTE_REMOVE, message, ar -> {
					if (ar.succeeded()) {
						vertx.eventBus().send(AddressConstant.ROUTE_DEPLOY, message);
					}
				});
			} catch (Exception e) {
				LOG.warn("route message is error", e);
			}
		});
		dList.forEach(kv -> {
			try {
				JsonObject message = new JsonObject(kv.getValue());
				vertx.eventBus().send(AddressConstant.ROUTE_REMOVE, message);
			} catch (Exception e) {
				LOG.warn("route message is error", e);
			}
		});
		cList.forEach(kv -> {
			try {
				JsonObject message = new JsonObject(kv.getValue());
				vertx.eventBus().send(AddressConstant.ROUTE_DEPLOY, message);
			} catch (Exception e) {
				LOG.warn("route message is error", e);
			}
		});
	}

	private void removeRouteWatch(String route) {
		Watch<KeyValueList> watch = routeWatchMap.remove(route);
		if (watch != null) {
			watch.stop();
		}
	}

	@SuppressWarnings("rawtypes")
	private void deployRoute(Message<JsonObject> message) {
		RouteOptions routeOptions = new RouteOptions(message.body());
		String error = checkRouteParams(routeOptions);
		if (error != null) {
			message.fail(400, error);
			return;
		}

		GatewayDeploy gatewayDeploy = clusterDeploy.get(routeOptions.getGateway());
		if (gatewayDeploy == null) {
			message.fail(503, "gateway is not start");
			return;
		}
		if (gatewayDeploy.containRoute(routeOptions.getRoute())) {
			message.reply(null);
			return;
		}

		GatewayOptions gatewayOptions = gatewayDeploy.getGatewayOptions();
		RouteDeploy routeDeploy = new RouteDeploy(gatewayOptions, routeOptions);

		List<Future> futures = new LinkedList<Future>();
		for (GatewayVerticle gatewayVerticle : gatewayDeploy.getGatewayVerticles()) {
			futures.add(gatewayVerticle.deployRoute(routeDeploy));
		}
		CompositeFuture.all(futures).onComplete(ar -> {
			if (ar.succeeded()) {
				LOG.info("deploy route success : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());

				gatewayDeploy.registRouteDeploy(routeDeploy);
				message.reply(null);
			} else {
				LOG.warn("deploy route failure : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
				message.fail(500, ar.cause().getMessage());
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void removeRoute(Message<JsonObject> message) {
		RouteOptions routeOptions = new RouteOptions(message.body());
		String error = checkRouteParams(routeOptions);
		if (error != null) {
			message.fail(400, error);
			return;
		}

		GatewayDeploy gatewayDeploy = clusterDeploy.get(routeOptions.getGateway());
		if (gatewayDeploy == null) {
			message.fail(503, "gateway is not start");
			return;
		}
		if (!gatewayDeploy.containRoute(routeOptions.getRoute())) {
			message.reply(null);
			return;
		}

		RouteDeploy routeDeploy = gatewayDeploy.searchRoute(routeOptions.getRoute());
		List<Future> futures = new LinkedList<Future>();
		for (GatewayVerticle gatewayVerticle : gatewayDeploy.getGatewayVerticles()) {
			futures.add(gatewayVerticle.removeRoute(routeDeploy));
		}
		CompositeFuture.all(futures).onComplete(ar -> {
			if (ar.succeeded()) {
				LOG.info("remove route success : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
				gatewayDeploy.removeRouteDeploy(routeDeploy);
				message.reply(null);
			} else {
				LOG.info("remove route failure : {} / {}", routeOptions.getGateway(), routeOptions.getRoute());
				message.fail(500, ar.cause().getMessage());
			}
		});
	}

	private String checkGatewayParams(GatewayOptions options) {
		if (options == null) {
			return "Gateway parameter is null";
		}
		if (!this.clusterCode.equals(options.getCluster())) {
			return "Gateway cluster is not same with " + clusterCode;
		}
		if (options.getGateway() == null || options.getGateway().isEmpty()) {
			return "Gateway name is empty";
		}
		return null;
	}

	private String checkRouteParams(RouteOptions routeOptions) {
		if (routeOptions == null) {
			return "route options is null";
		}
		if (!this.clusterCode.equals(routeOptions.getCluster())) {
			return "gateway cluster is not same with " + clusterCode;
		}
		if (routeOptions.getGateway() == null || routeOptions.getGateway().isEmpty()) {
			return "gateway name is empty";
		}
		if (routeOptions.getRoute() == null || routeOptions.getRoute().isEmpty()) {
			return "route name is empty";
		}
		return null;
	}

}
