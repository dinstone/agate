package com.dinstone.agate.manager.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.dao.AppDao;
import com.dinstone.agate.manager.model.AppEntity;

@Component
public class ManageService {

	private static final int APP_STATUS_START = 1;
	private static final int APP_STATUS_CLOSE = 0;

	@Autowired
	private AppDao appDao;

	public void createApp(AppEntity entity) throws BusinessException {
		// app param check
		appParamCheck(entity);

		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		appDao.create(entity);
	}

	private void appParamCheck(AppEntity entity) throws BusinessException {
		if (entity.getName() == null || entity.getName().isEmpty()) {
			throw new BusinessException(40101, "APP Name is empty");
		}
		if (entity.getCluster() == null || entity.getCluster().isEmpty()) {
			throw new BusinessException(40102, "Cluster is empty");
		}
		if (entity.getPrefix() == null || entity.getPrefix().isEmpty()) {
			throw new BusinessException(40103, "Prefix is empty");
		}
		if (entity.getPort() == null || entity.getPort() <= 0) {
			throw new BusinessException(40104, "Port must be great than 0");
		}
		if (entity.getServerConfig() != null && !checkJsonFormat(entity.getServerConfig())) {
			throw new BusinessException(40105, "ServerConfig is invalid json object");
		}
		if (entity.getClientConfig() != null && !checkJsonFormat(entity.getClientConfig())) {
			throw new BusinessException(40106, "ClientConfig is invalid json object");
		}
		// app logic check
		if (appDao.clusterAppExist(entity)) {
			throw new BusinessException(40107, "APP is not unique for cluster");
		}
		if (appDao.clusterPortExist(entity)) {
			throw new BusinessException(40107, "Port is not unique for cluster");
		}
	}

	public void updateApp(AppEntity entity) throws BusinessException {
		// app logic check
		if (entity.getId() == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}

		// app param check
		appParamCheck(entity);

		AppEntity ue = appDao.find(entity.getId());
		if (ue == null) {
			throw new BusinessException(40109, "can't find APP");
		}

		ue.setUpdateTime(new Date());
		ue.setName(entity.getName());
		ue.setCluster(entity.getCluster());
		ue.setHost(entity.getHost());
		ue.setPort(entity.getPort());
		ue.setPrefix(entity.getPrefix());
		ue.setRemark(entity.getRemark());
		ue.setServerConfig(entity.getServerConfig());
		ue.setClientConfig(entity.getClientConfig());

		appDao.update(ue);
	}

	private boolean checkJsonFormat(String config) throws BusinessException {
		try {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<AppEntity> appList() {
		return appDao.list();
	}

	public AppEntity getAppByID(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}
		return appDao.find(id);
	}

	public void deleteApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}

		appDao.delete(id);
	}

	public void startApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}
		AppEntity app = appDao.find(id);
		if (app == null) {
			return;
		}
		app.setStatus(APP_STATUS_START);
		app.setUpdateTime(new Date());
		appDao.updateStatus(app);
	}

	public void closeApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}
		AppEntity app = appDao.find(id);
		if (app == null) {
			return;
		}
		app.setStatus(APP_STATUS_CLOSE);
		app.setUpdateTime(new Date());
		appDao.updateStatus(app);
	}

}
