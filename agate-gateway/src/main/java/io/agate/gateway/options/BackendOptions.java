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
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class BackendOptions {

	/** 0:http reverse proxy; 1:http service discovery 2:grpc discovery */
	private int type;

	private int timeout;

	private String path;

	private String method;

	private List<String> urls;

	private JsonObject registry;

	private JsonObject connection;

	private List<ParamOptions> params;

	public BackendOptions(JsonObject json) {
		fromJson(json);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public List<ParamOptions> getParams() {
		return params;
	}

	public void setParams(List<ParamOptions> params) {
		this.params = params;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public JsonObject getRegistry() {
		return registry;
	}

	public void setRegistry(JsonObject registry) {
		this.registry = registry;
	}

	public JsonObject getConnection() {
		return connection;
	}

	public void setConnection(JsonObject connection) {
		this.connection = connection;
	}

	public void fromJson(JsonObject json) {
		for (java.util.Map.Entry<String, Object> member : json) {
			switch (member.getKey()) {
			case "timeout":
				if (member.getValue() instanceof Number) {
					this.setTimeout(((Number) member.getValue()).intValue());
				}
				break;
			case "type":
				if (member.getValue() instanceof Number) {
					this.setType(((Number) member.getValue()).intValue());
				}
				break;
			case "registry":
				if (member.getValue() instanceof JsonObject) {
					this.setRegistry((JsonObject) member.getValue());
				}
				break;
			case "connection":
				if (member.getValue() instanceof JsonObject) {
					this.setConnection((JsonObject) member.getValue());
				}
				break;
			case "method":
				if (member.getValue() instanceof String) {
					this.setMethod((String) member.getValue());
				}
				break;
			case "path":
				if (member.getValue() instanceof String) {
					this.setPath((String) member.getValue());
				}
				break;
			case "urls":
				if (member.getValue() instanceof JsonArray) {
					List<String> cl = new ArrayList<>();
					((JsonArray) member.getValue()).forEach(m -> {
						if (m instanceof String) {
							cl.add((String) m);
						}
					});
					this.setUrls(cl);
				}
				break;
			case "params":
				if (member.getValue() instanceof JsonArray) {
					List<ParamOptions> cl = new ArrayList<>();
					((JsonArray) member.getValue()).forEach(m -> {
						if (m instanceof JsonObject) {
							cl.add(new ParamOptions((JsonObject) m));
						}
					});
					this.setParams(cl);
				}
				break;
			}
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.put("timeout", timeout);
		json.put("type", type);
		if (path != null) {
			json.put("path", path);
		}
		if (method != null) {
			json.put("method", method);
		}
		if (urls != null) {
			json.put("urls", urls);
		}
		if (params != null) {
			json.put("params", params);
		}

		return json;
	}

	@Override
	public String toString() {
		return "ServiceOptions [type=" + type + ", timeout=" + timeout + ", method=" + method + ", urls=" + urls
				+ ", params=" + params + "]";
	}

}
