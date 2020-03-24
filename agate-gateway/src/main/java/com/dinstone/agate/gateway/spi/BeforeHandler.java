package com.dinstone.agate.gateway.spi;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface BeforeHandler extends Handler<RoutingContext> {

}
