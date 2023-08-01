package io.agate.manager.entity;

import java.util.Date;

public class AppEntity {

	private Integer id;

	private String name;

	private Integer gwId;

	private String json;

	private Date createTime;

	private Date updateTime;

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

	public Integer getGwId() {
		return gwId;
	}

	public void setGwId(Integer gwId) {
		this.gwId = gwId;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
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
		return "AppEntity [id=" + id + ", name=" + name + ", gwId=" + gwId + ", json=" + json + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}

}
