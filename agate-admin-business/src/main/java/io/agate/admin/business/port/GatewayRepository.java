package io.agate.admin.business.port;

import java.util.List;

import io.agate.admin.business.model.GatewayDefinition;

public interface GatewayRepository {

	boolean gatewayNameExist(GatewayDefinition definition);

	boolean hasGatewaysByClusterCode(String cluster);

	void create(GatewayDefinition entity);

	void update(GatewayDefinition entity);

	List<GatewayDefinition> list();

	GatewayDefinition find(Integer id);

	GatewayDefinition find(String cluster, String gateway);

	void delete(Integer id);

	void updateStatus(GatewayDefinition entity);

}