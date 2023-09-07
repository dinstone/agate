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
package io.agate.admin.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.vertx.web.annotation.BeanParam;
import com.dinstone.vertx.web.annotation.Context;
import com.dinstone.vertx.web.annotation.Get;
import com.dinstone.vertx.web.annotation.Produces;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.agate.domain.model.ClusterDefinition;
import io.agate.domain.service.BusinessException;
import io.agate.domain.service.ClusterService;
import io.vertx.ext.web.RoutingContext;

@Component
@WebHandler("/cluster")
public class ClusterResource {

	@Autowired
	private ClusterService clusterService;

	@Get
	@Produces("application/json")
	public List<ClusterDefinition> list(@Context RoutingContext rc) {
		return clusterService.clusterList();
	}

	@Get
	@Produces("application/json")
	public boolean add(@BeanParam ClusterDefinition clusterDefinition) throws BusinessException {
		clusterService.createCluster(clusterDefinition);
		return true;
	}
}
