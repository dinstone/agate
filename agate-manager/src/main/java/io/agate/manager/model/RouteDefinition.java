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
package io.agate.manager.model;

import java.util.List;

public class RouteDefinition {

	private Integer id;

	private int status;

	private Integer appId;

	private String name;

	private String remark;

	private FrontendDefinition frontend;

	private BackendDefinition backend;

	private List<PluginDefinition> plugins;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public FrontendDefinition getFrontend() {
		return frontend;
	}

	public void setFrontend(FrontendDefinition frontend) {
		this.frontend = frontend;
	}

	public BackendDefinition getBackend() {
		return backend;
	}

	public void setBackend(BackendDefinition backend) {
		this.backend = backend;
	}

	public List<PluginDefinition> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<PluginDefinition> plugins) {
		this.plugins = plugins;
	}

	@Override
	public String toString() {
		return "RouteDefination [id=" + id + ", name=" + name + ", remark=" + remark + ", appId=" + appId + ", status="
				+ status + ", frontend=" + frontend + ", backend=" + backend + ", plugins=" + plugins + "]";
	}

}
