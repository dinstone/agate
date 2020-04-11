package com.dinstone.agate.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.agate.manager.model.ApiConfig;
import com.dinstone.agate.manager.model.AppEntity;
import com.dinstone.agate.manager.service.BusinessException;
import com.dinstone.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/api")
public class ApiController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/list")
	public ModelAndView apiList(Integer appId) {
		try {
			ModelAndView mav = new ModelAndView("api/list");
			AppEntity appEntity = manageService.getAppById(appId);
			mav.addObject("app", appEntity);
			if (appEntity != null) {
				List<ApiConfig> apis = manageService.apiList(appId);
				mav.addObject("apis", apis);
			}
			return mav;
		} catch (Exception e) {
			return new ModelAndView("forward:/view/app/list");
		}
	}

	@RequestMapping("/create")
	public ModelAndView apiCreate(Integer appId) {
		try {
			ModelAndView mav = new ModelAndView("api/edit");
			AppEntity appEntity = manageService.getAppById(appId);
			mav.addObject("app", appEntity);
			return mav.addObject("action", "create");
		} catch (Exception e) {
			return new ModelAndView("forward:/view/api/list");
		}
	}

	@RequestMapping("/update")
	public ModelAndView apiUpdate(Integer appId, Integer apiId) {
		try {
			ApiConfig apiConfig = manageService.getApiById(apiId);
			AppEntity appEntity = manageService.getAppById(appId);
			ModelAndView mav = new ModelAndView("api/edit").addObject("action", "update");
			return mav.addObject("app", appEntity).addObject("api", apiConfig);
		} catch (Exception e) {
			return new ModelAndView("forward:/view/api/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView apiSave(ApiConfig apiConfig, String action) {
		ModelAndView mav = new ModelAndView("forward:/view/api/list");
		try {
			if ("create".equals(action)) {
				manageService.createApi(apiConfig);
			} else {
				manageService.updateApi(apiConfig);
			}
		} catch (BusinessException e) {
			AppEntity appEntity = manageService.getAppById(apiConfig.getAppId());
			mav = new ModelAndView("api/edit");
			mav.addObject("app", appEntity).addObject("api", apiConfig);
			mav.addObject("error", e.getMessage()).addObject("action", action);
		}
		return mav;
	}

	@RequestMapping("/detail")
	public ModelAndView apiDetail(Integer appId, Integer apiId) {
		ModelAndView mav = new ModelAndView("api/detail");
		try {
			ApiConfig apiConfig = manageService.getApiById(apiId);
			if (apiConfig != null) {
//				AppEntity appEntity = manageService.getAppById(apiConfig.getAppId());
				mav.addObject("api", apiConfig).addObject("action", "detail");
			}
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView apiDelete(Integer appId, Integer apiId) {
		try {
			manageService.deleteApi(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/api/list");
	}

	@RequestMapping("/start")
	public ModelAndView apiStart(Integer appId, Integer apiId) {
		try {
			manageService.startApi(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/api/list");
	}

	@RequestMapping("/close")
	public ModelAndView apiClose(Integer appId, Integer apiId) {
		try {
			manageService.closeApi(apiId);
		} catch (Exception e) {
		}
		return new ModelAndView("forward:/view/api/list");
	}

}
