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

package io.agate.gateway.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.agate.gateway.plugin.PluginOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RouteOptions {

	private String route;

	// ====================
	// Application options
	// ====================
	private String cluster;

	private String gateway;

	private String domain;

	private String prefix;

	// ====================
	// API options
	// ====================
	private RequestOptions request;

	private ServiceOptions service;

	private ResponseOptions response;

	// ====================
	// Plugin options
	// ====================
	private PluginOptions[] plugins;

	public RouteOptions(JsonObject json) {
		fromJson(json);
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public RequestOptions getRequest() {
		return request;
	}

	public void setRequest(RequestOptions frontend) {
		this.request = frontend;
	}

	public ResponseOptions getResponse() {
		return response;
	}

	public void setResponse(ResponseOptions response) {
		this.response = response;
	}

	public PluginOptions[] getPlugins() {
		return plugins;
	}

	public void setPlugins(PluginOptions[] options) {
		this.plugins = options;
	}

	public ServiceOptions getService() {
		return service;
	}

	public void setService(ServiceOptions service) {
		this.service = service;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		if (cluster != null) {
			json.put("cluster", cluster);
		}
		if (gateway != null) {
			json.put("gateway", gateway);
		}
		if (route != null) {
			json.put("route", route);
		}
		if (prefix != null) {
			json.put("prefix", prefix);
		}
		if (domain != null) {
			json.put("domain", domain);
		}
		if (request != null) {
			json.put("request", request.toJson());
		}
		if (response != null) {
			json.put("response", response.toJson());
		}
		if (service != null) {
			json.put("failures", service.toJson());
		}
		if (plugins != null) {
			json.put("plugins", Arrays.asList(plugins));
		}

		return json;
	}

	public void fromJson(JsonObject json) {
		for (java.util.Map.Entry<String, Object> member : json) {
			switch (member.getKey()) {
			case "domain":
				if (member.getValue() instanceof String) {
					this.setDomain((String) member.getValue());
				}
				break;
			case "prefix":
				if (member.getValue() instanceof String) {
					this.setPrefix((String) member.getValue());
				}
				break;
			case "gateway":
				if (member.getValue() instanceof String) {
					this.setGateway((String) member.getValue());
				}
				break;
			case "cluster":
				if (member.getValue() instanceof String) {
					this.setCluster((String) member.getValue());
				}
				break;
			case "route":
				if (member.getValue() instanceof String) {
					this.setRoute((String) member.getValue());
				}
				break;
			case "request":
				if (member.getValue() instanceof JsonObject) {
					this.setRequest(new RequestOptions((JsonObject) member.getValue()));
				}
				break;
			case "response":
				if (member.getValue() instanceof JsonObject) {
					this.setResponse(new ResponseOptions((JsonObject) member.getValue()));
				}
				break;
			case "service":
				if (member.getValue() instanceof JsonObject) {
					this.setService(new ServiceOptions((JsonObject) member.getValue()));
				}
				break;
			case "plugins":
				if (member.getValue() instanceof JsonArray) {
					List<PluginOptions> pol = new ArrayList<>();
					((JsonArray) member.getValue()).forEach(m -> {
						if (m instanceof JsonObject) {
							pol.add(new PluginOptions((JsonObject) m));
						}
					});
					this.setPlugins(pol.toArray(new PluginOptions[0]));
				}
				break;
			}
		}
	}

	@Override
	public String toString() {
		return "RouteOptions [route=" + route + ", cluster=" + cluster + ", gateway=" + gateway + ", domain=" + domain
				+ ", prefix=" + prefix + ", request=" + request + ", service=" + service + ", response=" + response
				+ ", plugins=" + Arrays.toString(plugins) + "]";
	}

}
