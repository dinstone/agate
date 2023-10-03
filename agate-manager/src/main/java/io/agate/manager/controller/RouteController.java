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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.domain.model.AppDefinition;
import io.agate.domain.model.RouteDefinition;
import io.agate.domain.service.BusinessException;
import io.agate.domain.service.ManageService;

@Controller
@RequestMapping("/view/route")
public class RouteController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/error")
	public ModelAndView error() {
		throw new RuntimeException("error test");
	}

	@RequestMapping("/list")
	public ModelAndView list(Integer appId) {
		AppDefinition appDefination = manageService.getAppById(appId);
		List<RouteDefinition> routeDefinations = manageService.routeList(appId);
		return new ModelAndView("route/list").addObject("app", appDefination).addObject("routes", routeDefinations);
	}

	@RequestMapping("/create")
	public ModelAndView create(Integer appId) {
		try {
			AppDefinition appDefination = manageService.getAppById(appId);
			ModelAndView mav = new ModelAndView("route/edit");
			return mav.addObject("app", appDefination).addObject("action", "create");
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer id) {
		try {
			RouteDefinition routeDefination = manageService.getRouteById(id);
			AppDefinition appDefination = manageService.getAppById(routeDefination.getAppId());
			ModelAndView mav = new ModelAndView("route/edit").addObject("action", "update");
			return mav.addObject("app", appDefination).addObject("route", routeDefination);
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(RouteDefinition defination, String action) {
		ModelAndView mav = new ModelAndView("forward:/view/route/list");
		try {
			if ("create".equals(action)) {
				manageService.createRoute(defination);
			} else {
				manageService.updateRoute(defination);
			}
		} catch (BusinessException e) {
			mav = new ModelAndView("route/edit");
			AppDefinition appDefination = manageService.getAppById(defination.getAppId());
			mav.addObject("app", appDefination).addObject("route", defination);
			mav.addObject("error", e.getMessage()).addObject("action", action);
		}
		return mav;
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer id) {
		ModelAndView mav = new ModelAndView("route/detail");
		try {
			RouteDefinition routeDefination = manageService.getRouteById(id);
			if (routeDefination != null) {
				AppDefinition appDefination = manageService.getAppById(routeDefination.getAppId());
				mav.addObject("route", routeDefination).addObject("app", appDefination);
			}
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView delete(Integer id) {
		try {
			manageService.deleteRoute(id);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

	@RequestMapping("/start")
	public ModelAndView start(Integer id) {
		try {
			manageService.startRoute(id);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

	@RequestMapping("/close")
	public ModelAndView close(Integer id) {
		try {
			manageService.closeRoute(id);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/route/list");
	}

}
