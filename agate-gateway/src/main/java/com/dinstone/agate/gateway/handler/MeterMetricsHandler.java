package com.dinstone.agate.gateway.handler;

import java.util.Arrays;
import java.util.List;

import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.spi.BeforeHandler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import io.vertx.ext.web.RoutingContext;

public class MeterMetricsHandler implements BeforeHandler {

	private Counter count;

	private Counter error;

	private Timer timer;

	public MeterMetricsHandler(ApiOptions apiOptions, MeterRegistry meterRegistry) {
		List<Tag> tags = Arrays.asList(Tag.of("app.name", apiOptions.getAppName()));
		this.count = meterRegistry.counter(apiOptions.getApiName() + "_count", tags);
		this.error = meterRegistry.counter(apiOptions.getApiName() + "_error", tags);
		this.timer = meterRegistry.timer(apiOptions.getApiName() + "_time", tags);
	}

	@Override
	public void handle(RoutingContext context) {
		Sample sample = Timer.start();
		context.addBodyEndHandler(v -> {
			sample.stop(timer);

			count.increment();
			if (isErrorCode(context.response().getStatusCode())) {
				error.increment();
			}
		});
		context.next();
	}

	private boolean isErrorCode(int statusCode) {
		return statusCode < 200 || statusCode >= 400;
	}

}
