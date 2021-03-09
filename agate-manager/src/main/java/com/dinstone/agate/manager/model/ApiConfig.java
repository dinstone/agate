/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.model;

public class ApiConfig {

	private Integer apiId;

	private Integer appId;

	private String name;

	private String remark;

	private BackendConfig backendConfig;

	private FrontendConfig frontendConfig;

	private int status;

	public Integer getApiId() {
		return apiId;
	}

	public void setApiId(Integer apiId) {
		this.apiId = apiId;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
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

	public BackendConfig getBackendConfig() {
		return backendConfig;
	}

	public void setBackendConfig(BackendConfig backendConfig) {
		this.backendConfig = backendConfig;
	}

	public FrontendConfig getFrontendConfig() {
		return frontendConfig;
	}

	public void setFrontendConfig(FrontendConfig frontendConfig) {
		this.frontendConfig = frontendConfig;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
