/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerFormat;

public class AccessLogHandler implements Handler<RoutingContext> {

	private static final Logger LOG = LoggerFactory.getLogger(AccessLogHandler.class);

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

	private final LoggerFormat format;

	public AccessLogHandler() {
		this(LoggerFormat.SHORT);
	}

	public AccessLogHandler(LoggerFormat format) {
		this.format = format;
	}

	private String getClientAddress(SocketAddress inetSocketAddress) {
		if (inetSocketAddress == null) {
			return null;
		}
		return inetSocketAddress.host();
	}

	private void log(RoutingContext context, long timestamp, String remoteClient, HttpVersion version,
			HttpMethod method, String uri) {

		String versionCode;
		switch (version) {
		case HTTP_1_0:
			versionCode = "HTTP/1.0";
			break;
		case HTTP_1_1:
			versionCode = "HTTP/1.1";
			break;
		case HTTP_2:
			versionCode = "HTTP/2.0";
			break;
		default:
			versionCode = "-";
		}

		int status = context.response().getStatusCode();
		long contentLength = context.response().bytesWritten();
		long costTime = System.currentTimeMillis() - timestamp;
		String accessTime = dateFormat.format(new Date(timestamp));

		String message = null;
		switch (format) {
		case DEFAULT:
			final MultiMap headers = context.request().headers();
			// as per RFC1945 the header is referer but it is not mandatory some
			// implementations use referrer
			String referrer = headers.contains("referrer") ? headers.get("referrer") : headers.get("referer");
			referrer = referrer == null ? "-" : referrer;

			String userAgent = context.request().headers().get("user-agent");
			userAgent = userAgent == null ? "-" : userAgent;

			message = String.format("%s - [%s] \"%s %s %s\" %d %d \"%s\" \"%s\" - %d", remoteClient, accessTime, method,
					uri, versionCode, status, contentLength, referrer, userAgent, costTime);
			break;
		case SHORT:
			message = String.format("%s - [%s] %s %s %s %d %d - %d", remoteClient, accessTime, method, uri, versionCode,
					status, contentLength, costTime);
			break;
		case TINY:
			message = String.format("[%s] %s %s %d %d - %d", accessTime, method, uri, status, contentLength, costTime);
			break;
		}
		doLog(status, message);
	}

	protected void doLog(int status, String message) {
		if (status >= 500) {
			LOG.error(message);
		} else if (status >= 400) {
			LOG.warn(message);
		} else {
			LOG.info(message);
		}
	}

	@Override
	public void handle(RoutingContext context) {
		// common logging data
		long timestamp = System.currentTimeMillis();

		String uri = context.request().uri();
		HttpMethod method = context.request().method();
		HttpVersion version = context.request().version();
		String remote = getClientAddress(context.request().remoteAddress());

		context.addBodyEndHandler(v -> log(context, timestamp, remote, version, method, uri));

		context.next();
	}

}
