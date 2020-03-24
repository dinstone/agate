package com.dinstone.agate.gateway.http;

import io.vertx.core.http.HttpMethod;

public class HttpUtil {

	public static boolean pathIsRegex(String routePath) {
		return routePath.indexOf("(") > 0 || routePath.indexOf("?<") > 0;
	}

	public static boolean hasBody(HttpMethod method) {
		return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE
				|| method == HttpMethod.PATCH || method == HttpMethod.TRACE;
	}
}
