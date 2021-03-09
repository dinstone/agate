/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.dao.ApiDao;
import com.dinstone.agate.manager.dao.AppDao;
import com.dinstone.agate.manager.model.ApiConfig;
import com.dinstone.agate.manager.model.ApiEntity;
import com.dinstone.agate.manager.model.AppEntity;
import com.dinstone.agate.manager.model.BackendConfig;
import com.dinstone.agate.manager.model.FrontendConfig;
import com.dinstone.agate.manager.model.ParamConfig;
import com.dinstone.agate.manager.utils.JacksonCodec;
import com.orbitz.consul.KeyValueClient;

@Component
public class ManageService {

	private static final int STATUS_START = 1;
	private static final int STATUS_CLOSE = 0;

	@Autowired
	private AppDao appDao;

	@Autowired
	private ApiDao apiDao;

	@Autowired
	private KeyValueClient keyValueClient;

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

	public AppEntity getAppById(Integer id) {
		if (id == null) {
			return null;
		}
		return appDao.find(id);
	}

	public void deleteApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}

		// close APIs
		List<ApiEntity> ael = apiDao.list(id);
		if (ael != null) {
			for (ApiEntity entity : ael) {
				closeApi(entity.getApiId());
			}
		}

		// close APP
		closeApp(id);

		// delete APIs and APP
		apiDao.deleteByAppId(id);
		appDao.delete(id);
	}

	public void startApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}
		AppEntity app = appDao.find(id);
		if (app != null && app.getStatus() == STATUS_CLOSE) {
			app.setStatus(STATUS_START);
			app.setUpdateTime(new Date());
			appDao.updateStatus(app);

			String key = "agate/apps/" + app.getCluster() + "/" + app.getName();
			String value = convertAppOptions(app);
			keyValueClient.putValue(key, value);
		}
	}

	private String convertAppOptions(AppEntity appEntity) {
		Map<String, Object> appOptions = new HashMap<>();
		appOptions.put("cluster", appEntity.getCluster());
		appOptions.put("appName", appEntity.getName());
		appOptions.put("prefix", appEntity.getPrefix());
		appOptions.put("remark", appEntity.getRemark());

		Map<String, Object> serverOptions = null;
		if (appEntity.getServerConfig() != null && !appEntity.getServerConfig().isEmpty()) {
			serverOptions = JacksonCodec.decodeValue(appEntity.getServerConfig(), Map.class);
		} else {
			serverOptions = new HashMap<>();
		}
		serverOptions.put("host", appEntity.getHost());
		serverOptions.put("port", appEntity.getPort());
		appOptions.put("serverOptions", serverOptions);

		if (appEntity.getClientConfig() != null && !appEntity.getClientConfig().isEmpty()) {
			Map<String, Object> clientOptions = JacksonCodec.decodeValue(appEntity.getClientConfig(), Map.class);
			appOptions.put("clientOptions", clientOptions);
		}

		return JacksonCodec.encode(appOptions);
	}

	public void closeApp(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "APP id is invalid");
		}
		AppEntity app = appDao.find(id);
		if (app != null && app.getStatus() == STATUS_START) {
			app.setStatus(STATUS_CLOSE);
			app.setUpdateTime(new Date());
			appDao.updateStatus(app);

			String key = "agate/apps/" + app.getCluster() + "/" + app.getName();
			keyValueClient.deleteKey(key);
		}
	}

	public List<ApiConfig> apiList(Integer appId) {
		List<ApiConfig> apiConfigs = new LinkedList<>();

		List<ApiEntity> aes = apiDao.list(appId);
		if (aes != null) {
			for (ApiEntity apiEntity : aes) {
				apiConfigs.add(covert(apiEntity));
			}
		}

		return apiConfigs;
	}

	private ApiConfig covert(ApiEntity apiEntity) {
		ApiConfig apiConfig = new ApiConfig();
		apiConfig.setApiId(apiEntity.getApiId());
		apiConfig.setAppId(apiEntity.getAppId());
		apiConfig.setName(apiEntity.getName());
		apiConfig.setRemark(apiEntity.getRemark());
		apiConfig.setStatus(apiEntity.getStatus());

		BackendConfig bc = JacksonCodec.decodeValue(apiEntity.getBackend(), BackendConfig.class);
		apiConfig.setBackendConfig(bc);

		FrontendConfig fc = JacksonCodec.decodeValue(apiEntity.getFrontend(), FrontendConfig.class);
		apiConfig.setFrontendConfig(fc);

		return apiConfig;
	}

	private ApiEntity covert(ApiConfig apiConfig) {
		ApiEntity apiEntity = new ApiEntity();
		apiEntity.setApiId(apiConfig.getApiId());
		apiEntity.setAppId(apiConfig.getAppId());
		apiEntity.setName(apiConfig.getName());
		apiEntity.setRemark(apiConfig.getRemark());

		apiEntity.setFrontend(JacksonCodec.encode(apiConfig.getFrontendConfig()));

		BackendConfig backendConfig = apiConfig.getBackendConfig();
		if (backendConfig.getParams() != null) {
			for (Iterator<ParamConfig> iterator = backendConfig.getParams().iterator(); iterator.hasNext();) {
				ParamConfig pc = iterator.next();
				if (pc == null || pc.getFeParamName() == null || pc.getBeParamName() == null) {
					iterator.remove();
				}
			}
		}
		apiEntity.setBackend(JacksonCodec.encode(backendConfig));

		return apiEntity;
	}

	public void createApi(ApiConfig apiConfig) throws BusinessException {
		if (apiConfig.getAppId() == null) {
			throw new BusinessException(40200, "APP id is invalid");
		}
		if (apiConfig.getName() == null || apiConfig.getName().isEmpty()) {
			throw new BusinessException(40201, "API name is empty");
		}
		FrontendConfig frontendConfig = apiConfig.getFrontendConfig();
		if (frontendConfig == null) {
			throw new BusinessException(40202, "frontend config is null");
		}
		if (frontendConfig.getPath() == null || frontendConfig.getPath().isEmpty()) {
			throw new BusinessException(40203, "frontend path is empty");
		}
		BackendConfig backendConfig = apiConfig.getBackendConfig();
		if (backendConfig == null) {
			throw new BusinessException(40204, "backend config is null");
		}
		if (backendConfig.getUrls() == null || backendConfig.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}

		for (Iterator<String> iterator = backendConfig.getUrls().iterator(); iterator.hasNext();) {
			String next = iterator.next();
			if (next == null || next.isEmpty()) {
				iterator.remove();
			}
		}
		if (backendConfig.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}

		if (apiDao.apiNameExist(apiConfig.getName())) {
			throw new BusinessException(40206, "API name is invalid");
		}

		AppEntity app = appDao.find(apiConfig.getAppId());
		if (app == null) {
			return;
		}

		frontendConfig.setPrefix(app.getPrefix());
		ApiEntity apiEntity = covert(apiConfig);
		Date now = new Date();
		apiEntity.setCreateTime(now);
		apiEntity.setUpdateTime(now);

		apiDao.create(apiEntity);
	}

	public void updateApi(ApiConfig apiConfig) throws BusinessException {
		FrontendConfig frontendConfig = apiConfig.getFrontendConfig();
		if (frontendConfig == null) {
			throw new BusinessException(40202, "frontend config is null");
		}
		if (frontendConfig.getPath() == null || frontendConfig.getPath().isEmpty()) {
			throw new BusinessException(40203, "frontend path is empty");
		}
		BackendConfig backendConfig = apiConfig.getBackendConfig();
		if (backendConfig == null) {
			throw new BusinessException(40204, "backend config is null");
		}
		if (backendConfig.getUrls() == null || backendConfig.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}
		for (Iterator<String> iterator = backendConfig.getUrls().iterator(); iterator.hasNext();) {
			String next = iterator.next();
			if (next == null || next.isEmpty()) {
				iterator.remove();
			}
		}
		if (backendConfig.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}

		ApiEntity apiEntity = apiDao.find(apiConfig.getApiId());
		if (apiEntity == null) {
			throw new BusinessException(40207, "can't find API");
		}
		AppEntity appEntity = appDao.find(apiEntity.getAppId());
		if (appEntity == null) {
			throw new BusinessException(40208, "can't find APP");
		}
		
		frontendConfig.setPrefix(appEntity.getPrefix());
		ApiEntity ae = covert(apiConfig);
		apiEntity.setRemark(ae.getRemark());
		apiEntity.setFrontend(ae.getFrontend());
		apiEntity.setBackend(ae.getBackend());
		apiEntity.setUpdateTime(new Date());

		apiDao.update(apiEntity);
	}

	public ApiConfig getApiById(Integer apiId) {
		ApiEntity ae = apiDao.find(apiId);
		if (ae != null) {
			return covert(ae);
		}
		return null;
	}

	public void deleteApi(Integer apiId) {
		closeApi(apiId);
		apiDao.delete(apiId);
	}

	public void startApi(Integer id) {
		ApiEntity api = apiDao.find(id);
		if (api != null && api.getStatus() == STATUS_CLOSE) {
			api.setStatus(STATUS_START);
			apiDao.updateStatus(api);

			AppEntity app = appDao.find(api.getAppId());

			String key = "agate/apis/" + app.getName() + "/" + api.getName();
			String value = convertApiOption(app, api);
			keyValueClient.putValue(key, value);
		}
	}

	private String convertApiOption(AppEntity appEntity, ApiEntity apiEntity) {
		Map<String, Object> apiOptions = new HashMap<>();
		apiOptions.put("cluster", appEntity.getCluster());
		apiOptions.put("appName", appEntity.getName());
		apiOptions.put("apiName", apiEntity.getName());

		apiOptions.put("frontend", JacksonCodec.decodeValue(apiEntity.getFrontend(), Map.class));
		apiOptions.put("backend", JacksonCodec.decodeValue(apiEntity.getBackend(), Map.class));

		return JacksonCodec.encode(apiOptions);
	}

	public void closeApi(Integer apiId) {
		ApiEntity api = apiDao.find(apiId);
		if (api != null && api.getStatus() == STATUS_START) {
			api.setStatus(STATUS_CLOSE);
			apiDao.updateStatus(api);

			AppEntity app = appDao.find(api.getAppId());

			String key = "agate/apis/" + app.getName() + "/" + api.getName();
			keyValueClient.deleteKey(key);
		}
	}

}
