package io.agate.manager.model;

import java.util.ArrayList;
import java.util.List;

public class ClusterDefinition {

	private Integer id;

	private String code;

	private String name;

	private List<InstanceDefinition> instances = new ArrayList<InstanceDefinition>();

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

	public List<InstanceDefinition> getInstances() {
		return instances;
	}

	public void setInstances(List<InstanceDefinition> instances) {
		this.instances = instances;
	}

	@Override
	public String toString() {
		return "ClusterDefination [id=" + id + ", code=" + code + ", name=" + name + ", instances=" + instances + "]";
	}

}
