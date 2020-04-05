package com.dinstone.agate.manager.model;

import java.util.Date;

public class AppEntity {

	private Integer id;

	private String name;

	private String cluster;

	private String remark;

	private String prefix;

	private String host;

	private Integer port;

	private int status;

	private String serverConfig;

	private String clientConfig;

	private Date createTime;

	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(String serverConfig) {
		this.serverConfig = serverConfig;
	}

	public String getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(String clientConfig) {
		this.clientConfig = clientConfig;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "AppEntity [name=" + name + ", cluster=" + cluster + ", prefix=" + prefix + ", host=" + host + ", port="
				+ port + "]";
	}

}
