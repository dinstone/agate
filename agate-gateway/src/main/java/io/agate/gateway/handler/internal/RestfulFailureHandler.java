/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
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
package io.agate.gateway.handler.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.handler.OrderedHandler;
import io.agate.gateway.http.RestfulUtil;
import io.vertx.ext.web.RoutingContext;

public class RestfulFailureHandler extends OrderedHandler {

	public RestfulFailureHandler(int order) {
		super(order);
	}

	private static final Logger LOG = LoggerFactory.getLogger(RestfulFailureHandler.class);

	@Override
	public void handle(RoutingContext rc) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("request [{}] error: {}", rc.request().path(), rc.failure());
		}

		int statusCode = rc.statusCode();
		if (statusCode == -1) {
			statusCode = 500;
		}
		if (!rc.response().ended()) {
			RestfulUtil.exception(rc, statusCode, rc.failure());
		}
	}

}
