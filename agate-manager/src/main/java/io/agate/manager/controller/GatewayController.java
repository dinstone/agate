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
package io.agate.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.manager.model.ClusterDefinition;
import io.agate.manager.model.GatewayDefinition;
import io.agate.manager.service.BusinessException;
import io.agate.manager.service.ClusterService;
import io.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/gateway")
public class GatewayController {

	@Autowired
	private ManageService manageService;

	@Autowired
	private ClusterService clusterService;

	@RequestMapping("/list")
	public ModelAndView list(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("gateway/list");
		List<GatewayDefinition> gateways = manageService.gatewayList();
		return mav.addObject("gateways", gateways);
	}

	@RequestMapping("/create")
	public ModelAndView create(HttpServletRequest request) {
		List<ClusterDefinition> clusters = clusterService.clusterList();
		ModelAndView mav = new ModelAndView("gateway/edit");
		return mav.addObject("action", "create").addObject("clusters", clusters);
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer id, HttpServletRequest request) {
		List<ClusterDefinition> clusters = clusterService.clusterList();
		try {
			GatewayDefinition appEntity = manageService.getGatewayById(id);
			ModelAndView mav = new ModelAndView("gateway/edit").addObject("gateway", appEntity);
			return mav.addObject("action", "update").addObject("clusters", clusters);
		} catch (Exception e) {
			return new ModelAndView("gateway/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(GatewayDefinition defination, String action, HttpServletRequest request) {
		try {
			if ("create".equals(action)) {
				manageService.createGateway(defination);
			} else {
				manageService.updateGateway(defination);
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("forward:/view/gateway/" + action);

			List<ClusterDefinition> clusters = clusterService.clusterList();
			mav.addObject("error", e.getMessage()).addObject("clusters", clusters);
			return mav.addObject("gateway", defination).addObject("action", action);
		}
		return new ModelAndView("forward:/view/gateway/list");
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer id) {
		ModelAndView mav = new ModelAndView("gateway/detail");
		try {
			GatewayDefinition gateway = manageService.getGatewayById(id);
			ClusterDefinition cluster = clusterService.getClusterByCode(gateway.getCluster());
			mav.addObject("cluster", cluster);
			mav.addObject("gateway", gateway);
			mav.addObject("action", "detail");
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView delete(Integer id) {
		try {
			manageService.deleteGateway(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/gateway/list");
	}

	@RequestMapping("/start")
	public ModelAndView start(Integer id) {
		try {
			manageService.startGateway(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/gateway/list");
	}

	@RequestMapping("/close")
	public ModelAndView close(Integer id) {
		try {
			manageService.closeGateway(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/gateway/list");
	}

}
