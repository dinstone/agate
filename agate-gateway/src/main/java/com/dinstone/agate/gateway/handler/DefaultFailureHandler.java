package com.dinstone.agate.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.FailureHandler;

import io.vertx.ext.web.RoutingContext;

public class DefaultFailureHandler implements FailureHandler {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFailureHandler.class);

	public DefaultFailureHandler(ApiOptions api) {
	}

	@Override
	public void handle(RoutingContext rc) {
		int statusCode = rc.statusCode();
		if (statusCode == -1) {
			statusCode = 500;
		}
		rc.response().setStatusCode(statusCode);

		String statusMessage = getMessage(rc.failure());
		if (statusMessage != null) {
			rc.response().setStatusMessage(statusMessage);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("request {} error: {}", rc.request().uri(), statusMessage);
		}

		if (!rc.response().ended()) {
			rc.response().end();
		}
	}

	private String getMessage(Throwable failure) {
		if (failure == null) {
			return null;
		}

		return failure.getMessage();
	}

}
