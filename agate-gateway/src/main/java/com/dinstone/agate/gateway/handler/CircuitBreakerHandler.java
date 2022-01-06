package com.dinstone.agate.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.deploy.RouteDeploy;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class CircuitBreakerHandler implements BeforeHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerHandler.class);

	private CircuitBreaker circuitBreaker;

	private RouteOptions routeOptions;

	public CircuitBreakerHandler(RouteOptions routeOptions, CircuitBreaker circuitBreaker) {
		this.routeOptions = routeOptions;
		this.circuitBreaker = circuitBreaker;
	}

	@Override
	public void handle(RoutingContext rc) {
		circuitBreaker.<Void>executeWithFallback(promise -> {
			rc.addEndHandler(ar -> {
				if (rc.response().getStatusCode() >= 400) {
					LOG.warn("route={} status={}", routeOptions.getRoute(), rc.response().getStatusCode());
					promise.fail("status = " + rc.statusCode());
				} else {
					promise.complete();
				}
			});
			rc.next();
		}, t -> {
			// gateway time-out
			rc.fail(504, t);
			return null;
		});
	}

	public static CircuitBreakerHandler create(RouteDeploy deploy, Vertx vertx) {
		CircuitBreaker circuitBreaker = deploy.createCircuitBreaker(vertx);
		return new CircuitBreakerHandler(deploy.getRouteOptions(), circuitBreaker);
	}

}
