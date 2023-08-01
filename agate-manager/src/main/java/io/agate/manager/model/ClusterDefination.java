package io.agate.manager.model;

import java.util.ArrayList;
import java.util.List;

public class ClusterDefination {

	private Integer id;

	private String code;

	private String name;

	private List<InstanceDefination> instances = new ArrayList<InstanceDefination>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InstanceDefination> getInstances() {
		return instances;
	}

	public void setInstances(List<InstanceDefination> instances) {
		this.instances = instances;
	}

	@Override
	public String toString() {
		return "ClusterDefination [id=" + id + ", code=" + code + ", name=" + name + ", instances=" + instances + "]";
	}

}
