/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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

	List<GatewayDefinition> find(String name, int start, int size);

	int total(String name);

}