package com.dinstone.agate.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.google.common.util.concurrent.RateLimiter;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * @author dinstone
 *
 */
public class RateLimitHandler implements Handler<RoutingContext> {

	private static final Logger LOG = LoggerFactory.getLogger(RateLimitHandler.class);

	private RateLimiter limiter;

	private ApiOptions api;

	public RateLimitHandler(ApiOptions api) {
		this.api = api;
		if (api.getRateLimit().getPermitsPerSecond() > 0) {
			limiter = RateLimiter.create(api.getRateLimit().getPermitsPerSecond());
		}
	}

	@Override
	public void handle(RoutingContext rc) {
		if (limiter == null) {
			rc.next();
			return;
		}

		if (limiter.tryAcquire()) {
			rc.next();
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("{} rate limit than {}/s", api.getApiName(), limiter.getRate());
			}
			rc.response().setStatusCode(502).end("rate limit");
		}
	}

}
