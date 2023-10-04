package io.agate.admin.business.port;

import java.util.List;

import io.agate.admin.business.model.ClusterDefinition;

public interface ClusterRepository {

	boolean clusterNameExist(String code);

	void create(ClusterDefinition defination);

	void update(ClusterDefinition definition);

	List<ClusterDefinition> list();

	ClusterDefinition find(Integer id);

	ClusterDefinition find(String code);

	void delete(Integer id);

}