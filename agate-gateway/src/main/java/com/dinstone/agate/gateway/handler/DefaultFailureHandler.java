package com.dinstone.agate.gateway.handler;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.FailureHandler;

import io.vertx.ext.web.RoutingContext;

public class DefaultFailureHandler implements FailureHandler {

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
