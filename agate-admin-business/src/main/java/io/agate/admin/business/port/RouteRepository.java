package io.agate.admin.business.port;

import java.util.List;

import io.agate.admin.business.model.RouteDefinition;

public interface RouteRepository {

	boolean routeNameExist(String name);

	void create(RouteDefinition definition);

	void update(RouteDefinition definition);

	List<RouteDefinition> list();

	List<RouteDefinition> list(Integer appId);

	List<RouteDefinition> listByGatewayId(Integer gwId);

	RouteDefinition find(Integer id);

	void delete(Integer id);

	void updateStatus(RouteDefinition definition);

	void deleteByGatewayId(Integer gwId);

}