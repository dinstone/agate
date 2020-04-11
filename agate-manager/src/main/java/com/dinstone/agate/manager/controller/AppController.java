package com.dinstone.agate.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.agate.manager.model.AppEntity;
import com.dinstone.agate.manager.service.BusinessException;
import com.dinstone.agate.manager.service.ManageService;

@Controller
@RequestMapping("/view/app")
public class AppController {

	@Autowired
	private ManageService manageService;

	@RequestMapping("/list")
	public ModelAndView appList() {
		ModelAndView mav = new ModelAndView("app/list");
		List<AppEntity> apps = manageService.appList();
		return mav.addObject("apps", apps);
	}

	@RequestMapping("/create")
	public ModelAndView appCreate() {
		ModelAndView mav = new ModelAndView("app/edit");
		return mav.addObject("action", "create");
	}

	@RequestMapping("/update")
	public ModelAndView appUpdate(Integer id) {
		try {
			AppEntity appEntity = manageService.getAppById(id);
			ModelAndView mav = new ModelAndView("app/edit");
			mav.addObject("app", appEntity);
			return mav.addObject("action", "update");
		} catch (Exception e) {
			return new ModelAndView("app/list");
		}
	}

	@RequestMapping("/save")
	public ModelAndView appSave(AppEntity app, String action) {
		try {
			if ("create".equals(action)) {
				manageService.createApp(app);
			} else {
				manageService.updateApp(app);
			}
		} catch (BusinessException e) {
			ModelAndView mav = new ModelAndView("app/edit");
			mav.addObject("error", e.getMessage());
			return mav.addObject("app", app).addObject("action", action);
		}
		return new ModelAndView("forward:/view/app/list");
	}

	@RequestMapping("/detail")
	public ModelAndView appDetail(Integer id) {
		ModelAndView mav = new ModelAndView("app/detail");
		try {
			AppEntity appEntity = manageService.getAppById(id);
			mav.addObject("app", appEntity);
			mav.addObject("action", "detail");
		} catch (Exception e) {
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@RequestMapping("/delete")
	public ModelAndView appDelete(Integer id) {
		try {
			manageService.deleteApp(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/app/list");
	}

	@RequestMapping("/start")
	public ModelAndView appStart(Integer id) {
		try {
			manageService.startApp(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/app/list");
	}

	@RequestMapping("/close")
	public ModelAndView appClose(Integer id) {
		try {
			manageService.closeApp(id);
		} catch (BusinessException e) {
		}
		return new ModelAndView("forward:/view/app/list");
	}

}
