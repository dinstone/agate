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
package io.agate.admin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.GatewayDefinition;
import io.agate.admin.business.param.GatewayQuery;
import io.agate.admin.business.param.PageList;
import io.agate.admin.business.service.ManageService;

@RestController
@RequestMapping("/gateway")
public class GatewayResource {

	@Autowired
	private ManageService manageService;

	@GetMapping("/list")
	public PageList<GatewayDefinition> list(GatewayQuery query) {
		return manageService.gatewayList(query);
	}

	@GetMapping("/detail")
	public GatewayDefinition detail(Integer id) {
		return manageService.getGatewayById(id);
	}

	@PostMapping("/save")
	public boolean save(@RequestBody GatewayDefinition gatewayDefinition) throws BusinessException {
		if (gatewayDefinition.getId() == null) {
			manageService.createGateway(gatewayDefinition);
		} else {
			manageService.updateGateway(gatewayDefinition);
		}
		return true;
	}

	@DeleteMapping("/delete")
	public boolean delete(@RequestBody Integer[] ids) throws BusinessException {
		if (ids != null) {
			for (Integer id : ids) {
				manageService.deleteGateway(id);
			}
		}
		return true;
	}

	@PutMapping("/start")
	public boolean start(Integer id) throws BusinessException {
		manageService.startGateway(id);
		return true;
	}

	@PutMapping("/close")
	public boolean close(Integer id) throws BusinessException {
		manageService.closeGateway(id);
		return true;
	}
}
