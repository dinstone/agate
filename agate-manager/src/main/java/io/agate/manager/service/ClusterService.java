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
package io.agate.manager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import io.agate.manager.dao.ClusterDao;
import io.agate.manager.dao.GatewayDao;
import io.agate.manager.entity.ClusterEntity;
import io.agate.manager.model.ClusterDefinition;
import io.agate.manager.model.InstanceDefinition;

@Component
public class ClusterService {

	private List<InstanceDefinition> clusterInstances = new CopyOnWriteArrayList<>();

	@Autowired
	private CatalogClient catalogClient;

	@Autowired
	private ClusterDao clusterDao;

	@Autowired
	private GatewayDao gatewayDao;

	public List<ClusterDefinition> clusterList() {
		List<ClusterEntity> ces = clusterDao.list();
		if (ces != null) {
			return ces.stream().map(ce -> convert(ce)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private ClusterDefinition convert(ClusterEntity ce) {
		ClusterDefinition cd = new ClusterDefinition();
		cd.setId(ce.getId());
		cd.setCode(ce.getCode());
		cd.setName(ce.getName());
		for (InstanceDefinition instance : clusterInstances) {
			if (cd.getCode().equals(instance.getClusterCode())) {
				cd.getInstances().add(instance);
			}
		}
		return cd;
	}

	public void clusterRefresh() {
		List<InstanceDefinition> instances = new ArrayList<>();
		ConsulResponse<List<CatalogService>> consulResponse = catalogClient.getService("agate-gateway");
		for (CatalogService e : consulResponse.getResponse()) {
			instances.add(createInstance(e.getServiceMeta()));
		}

		for (InstanceDefinition defination : instances) {
			if (!clusterInstances.contains(defination)) {
				clusterInstances.add(defination);
			}
		}
		clusterInstances.retainAll(instances);
	}

	private InstanceDefinition createInstance(Map<String, String> serviceMeta) {
		InstanceDefinition instance = new InstanceDefinition();
		try {
			if (serviceMeta != null) {
				instance.setInstanceId(serviceMeta.get("instanceId"));
				instance.setClusterCode(serviceMeta.get("cluster"));
			}
		} catch (Exception e) {
			// ignore
		}
		return instance;
	}

	public void createCluster(ClusterDefinition defination) throws BusinessException {
		// app param check
		if (defination.getCode() == null) {
			throw new BusinessException(40111, "Cluster code is invalid");
		}
		if (defination.getName() == null) {
			throw new BusinessException(40112, "Cluster name is invalid");
		}

		ClusterEntity entity = new ClusterEntity();
		entity.setCode(defination.getCode());
		entity.setName(defination.getName());
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		try {
			clusterDao.create(entity);
		} catch (Exception e) {
			throw new BusinessException(40110, "Cluster is exit");
		}
	}

	public void updateCluster(ClusterDefinition defination) throws BusinessException {
		// app logic check
		if (defination.getId() == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}
		if (defination.getName() == null) {
			throw new BusinessException(40112, "Cluster name is invalid");
		}

		// app param check
		ClusterEntity ce = clusterDao.find(defination.getId());
		if (ce == null) {
			throw new BusinessException(40109, "can't find cluster");
		}

		// ce.setCode(entity.getCode());
		ce.setName(defination.getName());
		ce.setUpdateTime(new Date());
		clusterDao.update(ce);
	}

	public void deleteCluster(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}
		ClusterEntity ce = clusterDao.find(id);
		if (ce != null) {
			if (gatewayDao.hasGatewaysByClusterCode(ce.getCode())) {
				throw new BusinessException(40111, "Cluster has gateways");
			}
		}
		clusterDao.delete(id);
	}

	public ClusterDefinition getClusterById(Integer id) {
		ClusterEntity e = clusterDao.find(id);
		return convert(e);
	}

	public ClusterDefinition getClusterByCode(String code) {
		ClusterEntity e = clusterDao.find(code);
		return convert(e);
	}

}
