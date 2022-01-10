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
