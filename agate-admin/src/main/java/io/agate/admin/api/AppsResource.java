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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.AppDefinition;
import io.agate.admin.business.param.AppDetail;
import io.agate.admin.business.param.PageList;
import io.agate.admin.business.param.PageQuery;
import io.agate.admin.business.service.ManageService;

@RestController
@RequestMapping("/apps")
public class AppsResource {

	@Autowired
	private ManageService manageService;

	@GetMapping("/list")
	public PageList<AppDefinition> list(PageQuery query) {
		return manageService.appList(query);
	}

	@GetMapping("/detail")
	public AppDetail detail(Integer id) {
		return manageService.getAppById(id);
	}

	@PostMapping("/save")
	public boolean add(@RequestBody AppDefinition appDefinition) throws BusinessException {
		if (appDefinition.getId() == null) {
			manageService.createApp(appDefinition);
		} else {
			manageService.updateApp(appDefinition);
		}
		return true;
	}

	@DeleteMapping("/delete")
	public boolean add(@RequestBody Integer[] ids) throws BusinessException {
		if (ids != null) {
			for (Integer id : ids) {
				manageService.deleteApp(id);
			}
		}
		return true;
	}
}
