package io.vertx.tracing.zipkin;

import brave.Tracing;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingOptions;

public class ZipkinTracingOptions extends TracingOptions {

	private HttpSenderOptions senderOptions = new HttpSenderOptions();
	private float probability = 1.0f;
	private String serviceName;

	private Tracing tracing;

	public ZipkinTracingOptions(Tracing tracing) {
		this.tracing = tracing;
	}

	public ZipkinTracingOptions(String serviceName) {
		this.serviceName = serviceName;
	}

	public ZipkinTracingOptions(JsonObject json) {
		super(json);
		if (json.getString("serviceName") != null) {
			serviceName = json.getString("serviceName");
		}
		if (json.getFloat("probability") != null) {
			probability = json.getFloat("probability");
		}
		if (json.getJsonObject("senderOptions") != null) {
			senderOptions = new HttpSenderOptions(json.getJsonObject("senderOptions"));
		}
	}

	public Tracing getTracing() {
		return tracing;
	}

	public void setTracing(Tracing tracing) {
		this.tracing = tracing;
	}

	public HttpSenderOptions getSenderOptions() {
		return senderOptions;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public float getProbability() {
		return probability;
	}

	public ZipkinTracingOptions setProbability(float probability) {
		this.probability = probability;
		return this;
	}

	public ZipkinTracingOptions setSenderOptions(HttpSenderOptions senderOptions) {
		this.senderOptions = senderOptions;
		return this;
	}

}
