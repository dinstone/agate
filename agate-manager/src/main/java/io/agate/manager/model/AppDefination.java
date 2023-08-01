package io.agate.manager.model;

public class AppDefination {

	private Integer id;

	private String name;

	private Integer gwId;

	private String domain;

	private String prefix;

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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
