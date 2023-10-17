package io.agate.admin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	public boolean add(@RequestBody GatewayDefinition gatewayDefinition) throws BusinessException {
		if (gatewayDefinition.getId() == null) {
			manageService.createGateway(gatewayDefinition);
		} else {
			manageService.updateGateway(gatewayDefinition);
		}
		return true;
	}

	@DeleteMapping("/delete")
	public boolean add(@RequestBody Integer[] ids) throws BusinessException {
		if (ids != null) {
			for (Integer id : ids) {
				manageService.deleteGateway(id);
			}
		}
		return true;
	}
}
