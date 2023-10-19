package io.agate.admin.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.ClusterDefinition;
import io.agate.admin.business.service.ClusterService;

@RestController
@RequestMapping("/cluster")
public class ClusterResource {

	@Autowired
	private ClusterService clusterService;

	@GetMapping("/list")
	public List<ClusterDefinition> list() {
		return clusterService.clusterList();
	}

	@PostMapping("/save")
	public boolean save(@RequestBody ClusterDefinition clusterDefinition) throws BusinessException {
		if (clusterDefinition.getId() == null) {
			clusterService.createCluster(clusterDefinition);
		} else {
			clusterService.updateCluster(clusterDefinition);
		}
		return true;
	}

	@DeleteMapping("/delete")
	public boolean delete(@RequestBody Integer[] ids) throws BusinessException {
		if (ids != null) {
			for (Integer id : ids) {
				clusterService.deleteCluster(id);
			}
		}
		return true;
	}
}
