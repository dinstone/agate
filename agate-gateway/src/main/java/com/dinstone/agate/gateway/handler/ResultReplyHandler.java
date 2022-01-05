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
package com.dinstone.agate.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.ContextConstants;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.spi.AfterHandler;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pipe;
import io.vertx.ext.web.RoutingContext;

/**
 * http route and proxy.
 *
 * @author dinstone
 *
 */
public class ResultReplyHandler implements AfterHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ResultReplyHandler.class);

	// private BackendOptions backendOptions;

	public ResultReplyHandler(RouteOptions routeOptions) {
		// backendOptions = apiOptions.getBackend();
	}

	@Override
	public void handle(RoutingContext rc) {
		HttpClientRequest beRequest = rc.get(ContextConstants.BACKEND_REQUEST);
		HttpClientResponse beResponse = rc.get(ContextConstants.BACKEND_RESPONSE);
		HttpServerResponse feResponse = rc.response();
		feResponse.setStatusCode(beResponse.statusCode());
		feResponse.headers().addAll(beResponse.headers());

		long len = getContentLength(beResponse);
		if (len == 0) {
			feResponse.end();
			return;
		}

		Pipe<Buffer> pipe = beResponse.pipe();
		pipe.to(feResponse).onFailure(t -> {
			LOG.error("backend response is error", t);
			beRequest.reset();
			feResponse.reset();
		});

	}

	private long getContentLength(HttpClientResponse response) {
		String tc = response.getHeader(HttpHeaders.TRANSFER_ENCODING);
		if ("chunked".equalsIgnoreCase(tc)) {
			return -1;
		}

		try {
			return Long.parseLong(response.getHeader(HttpHeaders.CONTENT_LENGTH));
		} catch (Exception e) {
			// ignore
		}
		return -1;
	}

}
