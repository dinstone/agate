package com.dinstone.agate.gateway.options;

import io.vertx.core.json.JsonObject;

public class RateLimitOptions {

	private double permitsPerSecond;

	public RateLimitOptions(JsonObject json) {
		fromJson(json);
	}

	public double getPermitsPerSecond() {
		return permitsPerSecond;
	}

	public void setPermitsPerSecond(double permitsPerSecond) {
		this.permitsPerSecond = permitsPerSecond;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.put("permitsPerSecond", permitsPerSecond);

		return json;
	}

	public void fromJson(JsonObject json) {
		for (java.util.Map.Entry<String, Object> member : json) {
			switch (member.getKey()) {
			case "permitsPerSecond":
				if (member.getValue() instanceof Number) {
					Number value = (Number) member.getValue();
					this.setPermitsPerSecond(value.doubleValue());
				}
				break;
			}
		}
	}

}
