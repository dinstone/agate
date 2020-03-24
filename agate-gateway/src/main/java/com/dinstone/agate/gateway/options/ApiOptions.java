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

import io.vertx.core.json.JsonObject;

public class ApiOptions {

	private String cluster;

	private String appName;

	private String apiName;

	private String remarks;

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

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
		if (frontend != null) {
			json.put("frontend", frontend.toJson());
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
			case "apiName":
				if (member.getValue() instanceof String) {
					this.setApiName((String) member.getValue());
				}
				break;
			case "backend":
				if (member.getValue() instanceof JsonObject) {
					this.setBackend(new BackendOptions((JsonObject) member.getValue()));
				}
				break;
			case "frontend":
				if (member.getValue() instanceof JsonObject) {
					this.setFrontend(new FrontendOptions((JsonObject) member.getValue()));
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
