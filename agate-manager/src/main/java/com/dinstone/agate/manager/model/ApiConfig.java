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
