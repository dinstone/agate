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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import io.agate.manager.model.AppDefination;
import io.agate.manager.model.RouteDefination;
import io.agate.manager.service.BusinessException;
import io.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/route")
public class RouteController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/list")
	public ModelAndView list(Integer appId) {
		AppDefination appDefination = manageService.getAppById(appId);
		List<RouteDefination> routeDefinations = manageService.routeList(appId);
		return new ModelAndView("route/list").addObject("app", appDefination).addObject("routes", routeDefinations);
	}

	@RequestMapping("/create")
	public ModelAndView create(Integer appId) {
		try {
			AppDefination appDefination = manageService.getAppById(appId);
			ModelAndView mav = new ModelAndView("route/edit");
			return mav.addObject("app", appDefination).addObject("action", "create");
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/update")
	public ModelAndView update(Integer id) {
		try {
			RouteDefination routeDefination = manageService.getRouteById(id);
			AppDefination appDefination = manageService.getAppById(routeDefination.getAppId());
			ModelAndView mav = new ModelAndView("route/edit").addObject("action", "update");
			return mav.addObject("app", appDefination).addObject("route", routeDefination);
		} catch (Exception e) {
			return new ModelAndView("forward:/view/route/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView save(RouteDefination defination, String action) {
		ModelAndView mav = new ModelAndView("forward:/view/route/list");
		try {
			if ("create".equals(action)) {
				manageService.createRoute(defination);
			} else {
				manageService.updateRoute(defination);
			}
		} catch (BusinessException e) {
			mav = new ModelAndView("route/edit");
			AppDefination appDefination = manageService.getAppById(defination.getAppId());
			mav.addObject("app", appDefination).addObject("route", defination);
			mav.addObject("error", e.getMessage()).addObject("action", action);
		}
		return mav;
	}

	@RequestMapping("/detail")
	public ModelAndView detail(Integer id) {
		ModelAndView mav = new ModelAndView("route/detail");
		try {
			RouteDefination routeDefination = manageService.getRouteById(id);
			if (routeDefination != null) {
				AppDefination appDefination = manageService.getAppById(routeDefination.getAppId());
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
