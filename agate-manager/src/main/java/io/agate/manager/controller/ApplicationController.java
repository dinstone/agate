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
package io.agate.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.manager.model.AppDefinition;
import io.agate.manager.model.GatewayDefinition;
import io.agate.manager.service.BusinessException;
import io.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/app")
public class ApplicationController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/list")
	public ModelAndView list(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("app/list");
		List<AppDefinition> apps = manageService.appList();
		return mav.addObject("apps", apps);
	}

	@RequestMapping("/create")
	public ModelAndView create(HttpServletRequest request) {
		List<GatewayDefinition> gateways = manageService.gatewayList();
		ModelAndView mav = new ModelAndView("app/edit");
		return mav.addObject("action", "create").addObject("gateways", gateways);
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer id, HttpServletRequest request) {
		List<GatewayDefinition> gateways = manageService.gatewayList();
		try {
			AppDefinition appEntity = manageService.getAppById(id);
			ModelAndView mav = new ModelAndView("app/edit").addObject("app", appEntity);
			return mav.addObject("action", "update").addObject("gateways", gateways);
		} catch (Exception e) {
			return new ModelAndView("app/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(AppDefinition app, String action, HttpServletRequest request) {
		try {
			if ("create".equals(action)) {
				manageService.createApp(app);
			} else {
				manageService.updateApp(app);
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("forward:/view/app/" + action);

			List<GatewayDefinition> gateways = manageService.gatewayList();
			mav.addObject("error", e.getMessage()).addObject("gateways", gateways);
			return mav.addObject("app", app).addObject("action", action);
		}
		return new ModelAndView("forward:/view/app/list");
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer id) {
		ModelAndView mav = new ModelAndView("app/detail");
		try {
			AppDefinition app = manageService.getAppById(id);
			GatewayDefinition gateway = manageService.getGatewayById(app.getGwId());
			mav.addObject("gateway", gateway);
			mav.addObject("app", app);
			mav.addObject("action", "detail");
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView delete(Integer id) {
		try {
			manageService.deleteApp(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/app/list");
	}

}
