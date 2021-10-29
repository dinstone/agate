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

import com.dinstone.agate.gateway.deploy.ApiDeploy;
import com.dinstone.agate.gateway.http.HttpUtil;
import com.dinstone.agate.gateway.http.QueryCoder;
import com.dinstone.agate.gateway.options.ApiOptions;
import com.dinstone.agate.gateway.options.ParamOptions;
import com.dinstone.agate.gateway.options.ParamType;
import com.dinstone.agate.gateway.options.RoutingOptions;
import com.dinstone.agate.gateway.spi.RouteHandler;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.httpproxy.ProxyRequest;
import io.vertx.httpproxy.ProxyResponse;

/**
 * http route and proxy.
 * 
 * @author dinstone
 *
 */
public class HttpProxyHandler2 implements RouteHandler {

	private static final Logger LOG = LoggerFactory.getLogger(HttpProxyHandler2.class);

	private final HttpClient httpClient;

	private final ApiOptions apiOptions;

	private final RoutingOptions backendOptions;

	private final CircuitBreaker circuitBreaker;

	private int count;

	public HttpProxyHandler2(ApiOptions apiOptions, HttpClient httpClient, CircuitBreaker circuitBreaker) {
		this.apiOptions = apiOptions;
		this.httpClient = httpClient;
		this.circuitBreaker = circuitBreaker;
		this.backendOptions = apiOptions.getRouting();
	}

	@Override
	public void handle(RoutingContext rc) {
		if (circuitBreaker != null) {
			circuitBreaker.<Void>executeWithFallback(promise -> {
				routing(rc).onComplete(promise);
			}, t -> {
				// gateway time-out
				rc.fail(504, t);
				return null;
			});
		} else {
			routing(rc);
		}
	}

	private Future<Void> routing(RoutingContext rc) {
		Promise<Void> promise = Promise.promise();

		HttpServerRequest serverRequest = rc.request();
		ProxyRequest proxyRequest = ProxyRequest.reverseProxy(serverRequest);

		// Encoding check
		Boolean chunked = HttpUtil.isChunked(serverRequest.headers());
		if (chunked == null) {
			rc.fail(400);
			promise.fail("bad request");
			return promise.future();
		}

		// locate url
		String originUrl = loadbalanceUrl(rc.request());
		if (originUrl == null) {
			rc.fail(503);
			promise.fail("service unavailable");
			return promise.future();
		}

		Map<String, String> pathParams = new HashMap<>();
		MultiMap queryParams = MultiMap.caseInsensitiveMultiMap();
		MultiMap headerParams = MultiMap.caseInsensitiveMultiMap();
		// parse params from frontend request
		List<ParamOptions> params = backendOptions.getParams();
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

		// replace param for url
		if (!pathParams.isEmpty()) {
			for (Entry<String, String> e : pathParams.entrySet()) {
				originUrl = originUrl.replace(":" + e.getKey(), e.getValue() == null ? "" : e.getValue());
			}
		}
		// set query params
		if (!queryParams.isEmpty()) {
			QueryCoder queryCoder = new QueryCoder(originUrl);
			for (Entry<String, String> e : queryParams) {
				queryCoder.addParam(e.getKey(), e.getValue());
			}
			originUrl = queryCoder.uri();
		} else if (serverRequest.query() != null) {
			// copy query frontend params to backend params
			QueryCoder queryCoder = new QueryCoder(originUrl);
			for (Entry<String, String> kve : rc.queryParams().entries()) {
				queryCoder.addParam(kve.getKey(), kve.getValue());
			}
			originUrl = queryCoder.uri();
		}

		// create backend request
		RequestOptions options = new RequestOptions();
		// set url
		options.setAbsoluteURI(originUrl);
		// set method
		options.setMethod(method(serverRequest.method()));
		// set timeout
		if (backendOptions.getTimeout() > 0) {
			options.setTimeout(backendOptions.getTimeout());
		}
		// set headers
		for (Entry<String, String> e : serverRequest.headers()) {
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

		httpClient.request(options).onSuccess(clientRequest -> {
			proxyRequest.setURI(options.getURI());
			proxyRequest.setMethod(method(serverRequest.method()));
			proxyRequest.send(clientRequest).onComplete(ar -> {
				if (ar.succeeded()) {
					ProxyResponse proxyResponse = ar.result();
					proxyResponse.send().onFailure(t -> {
						// service error
						LOG.error("API:" + apiOptions.getApiName() + ", pipe backend request error.", t);
					});
					promise.complete();
				} else {
					// service unavailable
					rc.fail(503, ar.cause());
					promise.fail(ar.cause());
				}
			});
		}).onFailure(t -> {
			rc.fail(502, t);// bad gateway
			promise.fail(t);
		});

		return promise.future();
	}

//	 private void handleProxyRequest(ProxyRequest proxyRequest, HttpClientRequest inboundRequest, Handler<AsyncResult<ProxyResponse>> handler) {
//		    ((ProxyRequestImpl)proxyRequest).send(inboundRequest, ar2 -> {
//		      if (ar2.succeeded()) {
//		        handler.handle(ar2);
//		      } else {
//		        proxyRequest.outboundRequest().response().setStatusCode(502).end();
//		        handler.handle(Future.failedFuture(ar2.cause()));
//		      }
//		    });
//		  }

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
		List<String> urls = backendOptions.getUrls();
		if (urls == null || urls.size() == 0) {
			return null;
		}

		if (count++ < 0) {
			count = 0;
		}

		int index = count % urls.size();
		return urls.get(index);
	}

	private HttpMethod method(HttpMethod httpMethod) {
		String method = backendOptions.getMethod();
		if (method != null && !method.isEmpty()) {
			return HttpMethod.valueOf(method.toUpperCase());
		}
		return httpMethod;
	}

	public static Handler<RoutingContext> create(ApiDeploy deploy, Vertx vertx) {
		HttpClient httpClient = deploy.createHttpClient(vertx);
		CircuitBreaker circuitBreaker = deploy.createCircuitBreaker(vertx);
		return new HttpProxyHandler2(deploy.getApiOptions(), httpClient, circuitBreaker);
	}

}
