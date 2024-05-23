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
package io.agate.admin.business.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.ClusterDefinition;
import io.agate.admin.business.model.InstanceDefinition;
import io.agate.admin.business.port.CatalogRepository;
import io.agate.admin.business.port.ClusterRepository;
import io.agate.admin.business.port.GatewayRepository;

@Component
public class ClusterService {

	private List<InstanceDefinition> clusterInstances = new CopyOnWriteArrayList<>();

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private ClusterRepository clusterRepository;

	@Autowired
	private GatewayRepository gatewayRepository;

	public List<ClusterDefinition> clusterList() {
		List<ClusterDefinition> cs = clusterRepository.list();
		if (cs != null) {
			cs.forEach(cd -> {
				for (InstanceDefinition instance : clusterInstances) {
					if (cd.getCode().equals(instance.getClusterCode())) {
						cd.addInstance(instance);
					}
				}
			});
			return cs;
		}
		return Collections.emptyList();
	}

	public void clusterRefresh() {
		List<Map<String, String>> metas = catalogRepository.getMetas("agate-gateway");
		if (metas == null) {
			return;
		}

		List<InstanceDefinition> instances = new LinkedList<>();
		for (Map<String, String> meta : metas) {
			InstanceDefinition instance = createInstance(meta);
			if (instance != null) {
				instances.add(instance);
			}
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
				instance.setClusterCode(serviceMeta.get("clusterCode"));
				instance.setManageHost(serviceMeta.get("manageHost"));
				instance.setManagePort(integer(serviceMeta.get("managePort")));
			}
		} catch (Exception e) {
			// ignore
		}
		return instance;
	}

	private int integer(String v) {
		if (v != null) {
			try {
				return Integer.parseInt(v);
			} catch (Exception e) {
				// ignore exception
			}
		}
		return 0;
	}

	public void createCluster(ClusterDefinition defination) throws BusinessException {
		// app param check
		if (defination.getCode() == null) {
			throw new BusinessException(40111, "Cluster code is invalid");
		}
		if (defination.getName() == null) {
			throw new BusinessException(40112, "Cluster name is invalid");
		}

		try {
			clusterRepository.create(defination);
		} catch (Exception e) {
			throw new BusinessException(40110, "Cluster is exit");
		}
	}

	public void updateCluster(ClusterDefinition definition) throws BusinessException {
		// app logic check
		if (definition.getId() == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}
		if (definition.getName() == null) {
			throw new BusinessException(40112, "Cluster name is invalid");
		}

		// app param check
		ClusterDefinition cd = clusterRepository.find(definition.getId());
		if (cd == null) {
			throw new BusinessException(40109, "can't find cluster");
		}

		// ce.setCode(entity.getCode());
		cd.setName(definition.getName());

		clusterRepository.update(definition);
	}

	public void deleteCluster(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}
		ClusterDefinition clusterDefinition = clusterRepository.find(id);
		if (clusterDefinition != null) {
			if (gatewayRepository.hasGatewaysByClusterCode(clusterDefinition.getCode())) {
				throw new BusinessException(40111, "Cluster has gateways");
			}
		}
		clusterRepository.delete(id);
	}

	public ClusterDefinition getClusterById(Integer id) {
		return clusterRepository.find(id);
	}

	public ClusterDefinition getClusterByCode(String code) {
		return clusterRepository.find(code);
	}

}
