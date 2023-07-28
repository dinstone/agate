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

package io.agate.gateway.plugin;

import io.vertx.core.json.JsonObject;

public class PluginOptions {

	private int order;

	private String plugin;

	private JsonObject options;

	public PluginOptions(JsonObject value) {
		fromJson(value);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getPlugin() {
		return plugin;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public JsonObject getOptions() {
		if (options == null) {
			options = new JsonObject();
		}
		return options;
	}

	public void setOptions(JsonObject options) {
		this.options = options;
	}

	public void fromJson(JsonObject json) {
		for (java.util.Map.Entry<String, Object> member : json) {
			Object value = member.getValue();
			switch (member.getKey()) {
			case "order":
				if (value instanceof Number) {
					this.setOrder(((Number) value).intValue());
				}
				break;
			case "plugin":
				if (value instanceof String) {
					this.setPlugin((String) value);
				}
				break;
			case "options":
				if (value instanceof JsonObject) {
					this.setOptions((JsonObject) value);
				}
				break;
			}
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.put("order", order);
		if (plugin != null) {
			json.put("plugin", plugin);
		}
		if (options != null) {
			json.put("options", options);
		}

		return json;
	}

	@Override
	public String toString() {
		return "PluginOptions [order=" + order + ", plugin=" + plugin + ", options=" + options + "]";
	}

}
