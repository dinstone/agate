/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.agate.manager.model.ApiConfig;
import com.dinstone.agate.manager.model.GatewayEntity;
import com.dinstone.agate.manager.service.BusinessException;
import com.dinstone.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/route")
public class RouteController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/list")
	public ModelAndView list() {
		List<ApiConfig> apis = manageService.apiList();
		return new ModelAndView("route/list").addObject("apis", apis);
	}

	@RequestMapping("/create")
	public ModelAndView create() {
		try {
			List<GatewayEntity> gateways = manageService.gatewayList();
			ModelAndView mav = new ModelAndView("route/edit");
			return mav.addObject("gateways", gateways).addObject("action", "create");
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer apiId) {
		try {
			ApiConfig apiConfig = manageService.getApiById(apiId);
			List<GatewayEntity> gateways = manageService.gatewayList();
			ModelAndView mav = new ModelAndView("route/edit").addObject("action", "update");
			return mav.addObject("gateways", gateways).addObject("api", apiConfig);
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(ApiConfig apiConfig, String action) {
		ModelAndView mav = new ModelAndView("forward:/view/route/list");
		try {
			if ("create".equals(action)) {
				manageService.createApi(apiConfig);
			} else {
				manageService.updateApi(apiConfig);
			}
		} catch (BusinessException e) {
			List<GatewayEntity> gateways = manageService.gatewayList();
			mav = new ModelAndView("route/edit");
			mav.addObject("gateways", gateways).addObject("api", apiConfig);
			mav.addObject("error", e.getMessage()).addObject("action", action);
		}
		return mav;
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer apiId) {
		ModelAndView mav = new ModelAndView("route/detail");
		try {
			ApiConfig apiConfig = manageService.getApiById(apiId);
			if (apiConfig != null) {
				GatewayEntity gateway = manageService.getGatewayById(apiConfig.getGwId());
				mav.addObject("api", apiConfig).addObject("gateway", gateway);
			}
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView delete(Integer apiId) {
		try {
			manageService.deleteApi(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

	@RequestMapping("/start")
	public ModelAndView start(Integer apiId) {
		try {
			manageService.startRoute(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

	@RequestMapping("/close")
	public ModelAndView close(Integer apiId) {
		try {
			manageService.closeRoute(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

}
