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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.context.ContextConstants;
import com.dinstone.agate.gateway.deploy.RouteDeploy;
import com.dinstone.agate.gateway.http.HttpUtil;
import com.dinstone.agate.gateway.http.QueryCoder;
import com.dinstone.agate.gateway.options.ParamOptions;
import com.dinstone.agate.gateway.options.ParamType;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.options.RoutingOptions;
import com.dinstone.agate.gateway.spi.RouteHandler;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.RoutingContext;

/**
 * http route and proxy.
 * 
 * @author dinstone
 *
 */
public class HttpProxyHandler implements RouteHandler {

	private static final Logger LOG = LoggerFactory.getLogger(HttpProxyHandler.class);

	private final HttpClient httpClient;

	private final RouteOptions routeOptions;

	private final RoutingOptions routingOptions;

	private int count;

	public HttpProxyHandler(RouteOptions routeOptions, HttpClient httpClient) {
		this.routeOptions = routeOptions;
		this.httpClient = httpClient;
		this.routingOptions = routeOptions.getRouting();
	}

	@Override
	public void handle(RoutingContext rc) {
		HttpServerRequest feRequest = rc.request().pause();

		Map<String, String> pathParams = new HashMap<>();
		MultiMap queryParams = MultiMap.caseInsensitiveMultiMap();
		MultiMap headerParams = MultiMap.caseInsensitiveMultiMap();
		// parse params from frontend request
		List<ParamOptions> params = routingOptions.getParams();
		if (params != null) {
			for (ParamOptions param : params) {
				if (ParamType.PATH == param.getBeParamType()) {
					String value = findParamValue(rc, param);
					pathParams.put(param.getBeParamName(), value);
				} else if (ParamType.QUERY == param.getBeParamType()) {
					String value = findParamValue(rc, param);
					queryParams.add(param.getBeParamName(), value);
				} else if (ParamType.HEADER == param.getBeParamType()) {
					String value = findParamValue(rc, param);
					headerParams.add(param.getBeParamName(), value);
				}
			}
		}

		// locate url
		String requestUrl = loadbalanceUrl(feRequest);
		// replace param for url
		if (!pathParams.isEmpty()) {
			for (Entry<String, String> e : pathParams.entrySet()) {
				requestUrl = requestUrl.replace(":" + e.getKey(), e.getValue() == null ? "" : e.getValue());
			}
		}
		// set query params
		if (!queryParams.isEmpty()) {
			QueryCoder queryCoder = new QueryCoder(requestUrl);
			for (Entry<String, String> e : queryParams) {
				queryCoder.addParam(e.getKey(), e.getValue());
			}
			requestUrl = queryCoder.uri();
		} else if (feRequest.query() != null) {
			// copy query frontend params to backend params
			QueryCoder queryCoder = new QueryCoder(requestUrl);
			for (Entry<String, String> kve : rc.queryParams().entries()) {
				queryCoder.addParam(kve.getKey(), kve.getValue());
			}
			requestUrl = queryCoder.uri();
		}

		// create backend request
		RequestOptions options = new RequestOptions();
		// set url
		options.setAbsoluteURI(requestUrl);
		// set method
		options.setMethod(method(feRequest.method()));
		// set timeout
		if (routingOptions.getTimeout() > 0) {
			options.setTimeout(routingOptions.getTimeout());
		}
		// set headers
		for (Entry<String, String> e : feRequest.headers()) {
			if (e.getKey().equalsIgnoreCase(HttpHeaders.HOST.toString())) {
				continue;
			}
			options.addHeader(e.getKey(), e.getValue());
		}
		if (headerParams.size() > 0) {
			for (Entry<String, String> e : headerParams) {
				options.addHeader(e.getKey(), e.getValue());
			}
		}

		// create http request
		httpClient.request(options).onSuccess(beRequest -> {
			// response handler
			beRequest.response().onComplete(ar -> {
				if (ar.succeeded()) {
					HttpClientResponse beResponse = ar.result().pause();
					rc.put(ContextConstants.BACKEND_REQUEST, beRequest);
					rc.put(ContextConstants.BACKEND_RESPONSE, beResponse);
					rc.next();
				} else {
					// Service Unavailable
					rc.fail(503, ar.cause());
				}
			});

			// transport body and send request
			if (HttpUtil.hasBody(beRequest.getMethod())) {
				feRequest.pipe().to(beRequest).onFailure(e -> {
					// service unavailable
					LOG.warn("route:" + routeOptions.getRoute() + ", pump backend request error.", e);
					beRequest.reset();
					rc.fail(503, e);
				});
			} else {
				beRequest.end();
			}
		}).onFailure(t -> {
			rc.fail(502, t);// bad gateway
		});

	}

	private String findParamValue(RoutingContext rc, ParamOptions param) {
		if (ParamType.PATH == param.getFeParamType()) {
			return rc.pathParam(param.getFeParamName());
		} else if (ParamType.QUERY == param.getFeParamType()) {
			return rc.queryParams().get(param.getFeParamName());
		} else if (ParamType.HEADER == param.getFeParamType()) {
			return rc.request().getHeader(param.getFeParamName());
		}

		return null;
	}

	private String loadbalanceUrl(HttpServerRequest inRequest) {
		List<String> urls = routingOptions.getUrls();
		if (count++ < 0) {
			count = 0;
		}

		int index = count % urls.size();
		return urls.get(index);
	}

	private HttpMethod method(HttpMethod httpMethod) {
		String method = routingOptions.getMethod();
		if (method != null && !method.isEmpty()) {
			return HttpMethod.valueOf(method.toUpperCase());
		}
		return httpMethod;
	}

	public static Handler<RoutingContext> create(RouteDeploy deploy, Vertx vertx) {
		HttpClient httpClient = deploy.createHttpClient(vertx);
		RouteOptions routeOptions = deploy.getRouteOptions();
		return new HttpProxyHandler(routeOptions, httpClient);
	}

}
