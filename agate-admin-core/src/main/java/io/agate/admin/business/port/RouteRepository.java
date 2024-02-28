/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	int total(Integer appId);

	List<RouteDefinition> find(Integer appId, int start, int size);

}