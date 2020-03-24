package com.dinstone.agate.gateway.spi;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface FailureHandler extends Handler<RoutingContext> {

}
