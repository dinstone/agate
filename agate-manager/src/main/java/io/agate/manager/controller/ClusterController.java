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
package io.agate.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.ClusterDefinition;
import io.agate.admin.business.service.ClusterService;

@Controller
@RequestMapping("/view/cluster")
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@RequestMapping("/list")
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView("cluster/list");
		List<ClusterDefinition> clusters = clusterService.clusterList();
		return mav.addObject("clusters", clusters);
	}

	@RequestMapping("/create")
	public ModelAndView create() {
		ModelAndView mav = new ModelAndView("cluster/edit");
		return mav.addObject("action", "create");
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer id) {
		try {
			ClusterDefinition cluster = clusterService.getClusterById(id);
			ModelAndView mav = new ModelAndView("cluster/edit").addObject("cluster", cluster);
			return mav.addObject("action", "update");
		} catch (Exception e) {
			return new ModelAndView("cluster/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(ClusterDefinition clusterDefination, String action) {
		try {
			if ("create".equals(action)) {
				clusterService.createCluster(clusterDefination);
			} else {
				clusterService.updateCluster(clusterDefination);
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("forward:/view/cluster/" + action);
			mav.addObject("error", e.getMessage());
			return mav.addObject("cluster", clusterDefination).addObject("action", action);
		}
		return new ModelAndView("forward:/view/cluster/list");
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer id) {
		try {
			ClusterDefinition cluster = clusterService.getClusterById(id);
			return new ModelAndView("cluster/detail").addObject("cluster", cluster);
		} catch (Exception e) {
			return new ModelAndView("forward:/view/cluster/list");
		}
	}

	@RequestMapping("/delete")
	public ModelAndView delete(Integer id) {
		try {
			clusterService.deleteCluster(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/cluster/list");
	}

}
