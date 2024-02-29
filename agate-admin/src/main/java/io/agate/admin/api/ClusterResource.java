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
package io.agate.admin.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.ClusterDefinition;
import io.agate.admin.business.service.ClusterService;

@RestController
@RequestMapping("/cluster")
public class ClusterResource {

	@Autowired
	private ClusterService clusterService;

	@GetMapping("/list")
	public List<ClusterDefinition> list() {
		return clusterService.clusterList();
	}

	@PostMapping("/save")
	public boolean save(@RequestBody ClusterDefinition clusterDefinition) throws BusinessException {
		if (clusterDefinition.getId() == null) {
			clusterService.createCluster(clusterDefinition);
		} else {
			clusterService.updateCluster(clusterDefinition);
		}
		return true;
	}

	@DeleteMapping("/delete")
	public boolean delete(@RequestBody Integer[] ids) throws BusinessException {
		if (ids != null) {
			for (Integer id : ids) {
				clusterService.deleteCluster(id);
			}
		}
		return true;
	}
}
