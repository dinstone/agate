/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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
package com.dinstone.agate.gateway.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ApiOptions {

	private String cluster;

	private String appName;

	private String prefix;

	// ====================

	private String apiName;

	private String path;

	private String method;

	private String[] consumes;

	private String[] produces;

	// ====================
	private FrontendOptions frontend;

	private BackendOptions backend;

	// ====================
	private RateLimitOptions rateLimit;

	public ApiOptions(JsonObject json) {
		fromJson(json);
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPrefix() {
		if (prefix == null || prefix.isEmpty()) {
			prefix = "/*";
		}
		if (!prefix.endsWith("*")) {
			prefix = prefix + "*";
		}
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getConsumes() {
		return consumes;
	}

	public void setConsumes(String[] consumes) {
		this.consumes = consumes;
	}

	public String[] getProduces() {
		return produces;
	}

	public void setProduces(String[] produces) {
		this.produces = produces;
	}

	public FrontendOptions getFrontend() {
		return frontend;
	}

	public void setFrontend(FrontendOptions frontend) {
		this.frontend = frontend;
	}

	public BackendOptions getBackend() {
		return backend;
	}

	public void setBackend(BackendOptions backend) {
		this.backend = backend;
	}

	public RateLimitOptions getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(RateLimitOptions rateLimit) {
		this.rateLimit = rateLimit;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		if (appName != null) {
			json.put("appName", appName);
		}
		if (cluster != null) {
			json.put("cluster", cluster);
		}
		if (prefix != null) {
			json.put("prefix", prefix);
		}
		if (apiName != null) {
			json.put("apiName", apiName);
		}
		if (path != null) {
			json.put("path", path);
		}
		if (method != null) {
			json.put("method", method);
		}
		if (consumes != null) {
			json.put("consumes", Arrays.asList(consumes));
		}
		if (produces != null) {
			json.put("produces", Arrays.asList(produces));
		}
		if (backend != null) {
			json.put("backend", backend.toJson());
		}
		if (rateLimit != null) {
			json.put("rateLimit", rateLimit.toJson());
		}

		return json;
	}

	public void fromJson(JsonObject json) {
		for (java.util.Map.Entry<String, Object> member : json) {
			switch (member.getKey()) {
			case "appName":
				if (member.getValue() instanceof String) {
					this.setAppName((String) member.getValue());
				}
				break;
			case "cluster":
				if (member.getValue() instanceof String) {
					this.setCluster((String) member.getValue());
				}
				break;
			case "prefix":
				if (member.getValue() instanceof String) {
					this.setPrefix((String) member.getValue());
				}
				break;
			case "apiName":
				if (member.getValue() instanceof String) {
					this.setApiName((String) member.getValue());
				}
				break;
			case "path":
				if (member.getValue() instanceof String) {
					this.setPath((String) member.getValue());
				}
				break;
			case "method":
				if (member.getValue() instanceof String) {
					this.setMethod((String) member.getValue());
				}
				break;
			case "consumes":
				if (member.getValue() instanceof JsonArray) {
					List<String> cl = new ArrayList<>();
					((JsonArray) member.getValue()).forEach(m -> {
						if (m instanceof String) {
							cl.add((String) m);
						}
					});
					this.setConsumes(cl.toArray(new String[0]));
				}
				break;
			case "produces":
				if (member.getValue() instanceof JsonArray) {
					List<String> cl = new ArrayList<>();
					((JsonArray) member.getValue()).forEach(m -> {
						if (m instanceof String) {
							cl.add((String) m);
						}
					});
					this.setProduces(cl.toArray(new String[0]));
				}
				break;
			case "backend":
				if (member.getValue() instanceof JsonObject) {
					this.setBackend(new BackendOptions((JsonObject) member.getValue()));
				}
				break;
			case "rateLimit":
				if (member.getValue() instanceof JsonObject) {
					this.setRateLimit(new RateLimitOptions((JsonObject) member.getValue()));
				}
				break;
			}
		}
	}

}
