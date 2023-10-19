package io.agate.admin.business.port;

import java.util.List;

import io.agate.admin.business.model.AppDefinition;

public interface AppRepository {

	boolean appNameExist(String name);

	void create(AppDefinition entity);

	void update(AppDefinition entity);

	List<AppDefinition> list();

	AppDefinition find(Integer id);

	void delete(Integer id);

	int total();

	List<AppDefinition> find(int start, int size);

}