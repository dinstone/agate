/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.dao.ClusterDao;
import com.dinstone.agate.manager.dao.GatewayDao;
import com.dinstone.agate.manager.model.ClusterEntity;
import com.dinstone.agate.manager.model.NodeEntity;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

@Component
public class ClusterService {

	private List<NodeEntity> clusterNodes = new CopyOnWriteArrayList<>();

	@Autowired
	private CatalogClient catalogClient;

	@Autowired
	private ClusterDao clusterDao;

	@Autowired
	private GatewayDao gatewayDao;

	public List<ClusterEntity> clusterList() {
		return clusterDao.list();
	}

	public List<ClusterEntity> clusterStatus() {
		List<ClusterEntity> ces = clusterDao.list();
		if (ces != null) {
			for (ClusterEntity ce : ces) {
				findNodes(ce);
			}
		}
		return ces;
	}

	private void findNodes(ClusterEntity ce) {
		for (NodeEntity ne : clusterNodes) {
			if (ce.getCode().equals(ne.getClusterCode())) {
				ce.getNodes().add(ne);
			}
		}
	}

	public void clusterRefresh() {
		List<NodeEntity> entities = new ArrayList<>();

		ConsulResponse<List<CatalogService>> consulResponse = catalogClient.getService("agate-gateway");
		for (CatalogService e : consulResponse.getResponse()) {
			NodeEntity node = createNode(e.getServiceMeta());
			entities.add(node);
		}

		clusterNodes.clear();
		clusterNodes.addAll(entities);
	}

	private NodeEntity createNode(Map<String, String> serviceMeta) {
		NodeEntity node = new NodeEntity();
		try {
			if (serviceMeta != null) {
				node.setInstanceId(serviceMeta.get("instanceId"));
				node.setClusterCode(serviceMeta.get("clusterCode"));
			}
		} catch (Exception e) {
			// ignore
		}
		return node;
	}

	public void createCluster(ClusterEntity entity) throws BusinessException {
		// app param check

		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		try {
			clusterDao.create(entity);
		} catch (Exception e) {
			throw new BusinessException(40110, "Cluster is exit");
		}
	}

	public void updateCluster(ClusterEntity entity) throws BusinessException {
		// app logic check
		if (entity.getId() == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}

		// app param check
		ClusterEntity ce = clusterDao.find(entity.getId());
		if (ce == null) {
			throw new BusinessException(40109, "can't find cluster");
		}

		// ce.setCode(entity.getCode());
		ce.setName(entity.getName());
		ce.setUpdateTime(new Date());
		clusterDao.update(ce);
	}

	public void deleteCluster(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "Cluster id is invalid");
		}
		ClusterEntity ce = clusterDao.find(id);
		if (ce != null) {
			if (gatewayDao.gatewayCodeExist(ce.getCode())) {
				throw new BusinessException(40111, "Cluster id is invalid");
			}
		}
		clusterDao.delete(id);
	}

	public ClusterEntity getClusterById(Integer id) {
		return clusterDao.find(id);
	}

	public ClusterEntity getClusterByCode(String code) {
		return clusterDao.find(code);
	}

}
