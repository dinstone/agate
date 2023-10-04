/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.admin.business.model;

import java.util.List;

public class BackendDefinition {

	/** 0:http reverse proxy; 1:http service discovery */
	private int type;

	/**
	 * 0: RoundRobinLoadBalancer; 1: RandomLoadBalancer
	 */
	private int algorithm;

	private Integer timeout;

	private String method;

	private String path;

	private String registry;

	private String connection;

	private List<String> urls;

	private List<ParamDefinition> params;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
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

	public List<ParamDefinition> getParams() {
		return params;
	}

	public void setParams(List<ParamDefinition> params) {
		this.params = params;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public int getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(int algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public String toString() {
		return "BackendDefination [path=" + path + ", type=" + type + ", algorithm=" + algorithm + ", timeout="
				+ timeout + ", method=" + method + ", registry=" + registry + ", connection=" + connection + ", urls="
				+ urls + ", params=" + params + "]";
	}

}
