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
package io.agate.domain.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.agate.domain.dao.AppDao;
import io.agate.domain.dao.GatewayDao;
import io.agate.domain.dao.RouteDao;
import io.agate.domain.entity.AppEntity;
import io.agate.domain.entity.GatewayEntity;
import io.agate.domain.entity.RouteEntity;
import io.agate.domain.model.AppDefinition;
import io.agate.domain.model.BackendDefinition;
import io.agate.domain.model.FrontendDefinition;
import io.agate.domain.model.GatewayDefinition;
import io.agate.domain.model.PluginDefinition;
import io.agate.domain.model.RouteDefinition;
import io.agate.domain.port.CatalogStore;
import io.agate.domain.utils.JacksonCodec;

@Component
public class ManageService {

	private static final int STATUS_START = 1;

	private static final int STATUS_CLOSE = 0;

	@Autowired
	private GatewayDao gatewayDao;

	@Autowired
	private RouteDao routeDao;

	@Autowired
	private AppDao appDao;

	@Autowired
	private CatalogStore catalogStore;

	public void createGateway(GatewayDefinition defination) throws BusinessException {
		// app param check
		GatewayEntity entity = gatewayParamCheck(defination);

		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		gatewayDao.create(entity);
	}

	private GatewayEntity gatewayParamCheck(GatewayDefinition defination) throws BusinessException {
		if (defination.getName() == null || defination.getName().isEmpty()) {
			throw new BusinessException(40101, "Gateway Name is empty");
		}
		if (defination.getCluster() == null) {
			throw new BusinessException(40102, "Cluster is empty");
		}
		if (defination.getPort() == null || defination.getPort() <= 0) {
			throw new BusinessException(40104, "Port must be great than 0");
		}
		GatewayEntity entity = convert(defination);
		// app logic check
		if (gatewayDao.gatewayNameExist(entity)) {
			throw new BusinessException(40107, "Gateway Name is not unique for cluster");
		}
		return entity;
	}

	private GatewayEntity convert(GatewayDefinition gd) {
		GatewayEntity ge = new GatewayEntity();
		ge.setCluster(gd.getCluster());
		ge.setId(gd.getId());
		ge.setName(gd.getName());
		ge.setStatus(gd.getStatus());
		ge.setJson(JacksonCodec.encode(gd));
		return ge;
	}

	public void updateGateway(GatewayDefinition defination) throws BusinessException {
		// app logic check
		if (defination.getId() == null) {
			throw new BusinessException(40108, "Gateway id is invalid");
		}

		// app param check

		GatewayEntity entity = gatewayParamCheck(defination);
		entity.setUpdateTime(new Date());

		gatewayDao.update(entity);
	}

	public List<GatewayDefinition> gatewayList() {
		List<GatewayEntity> ges = gatewayDao.list();
		if (ges != null) {
			return ges.stream().map(ge -> convert(ge)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private GatewayDefinition convert(GatewayEntity ge) {
		GatewayDefinition gd = JacksonCodec.decode(ge.getJson(), GatewayDefinition.class);
		gd.setId(ge.getId());
		gd.setCluster(ge.getCluster());
		gd.setName(ge.getName());
		gd.setStatus(ge.getStatus());
		return gd;
	}

	public GatewayDefinition getGatewayById(Integer id) {
		GatewayEntity ge = gatewayDao.find(id);
		if (ge != null) {
			return convert(ge);
		}
		return null;
	}

	public void deleteGateway(Integer gwId) throws BusinessException {
		// close Gateway
		closeGateway(gwId);

		// close Routes
		List<RouteEntity> ael = routeDao.list(gwId);
		if (ael != null) {
			for (RouteEntity entity : ael) {
				closeRoute(entity.getId());
			}
		}

		// delete Gateway
		gatewayDao.delete(gwId);
	}

	public void startGateway(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "Gateway id is invalid");
		}
		GatewayEntity ge = gatewayDao.find(id);
		if (ge != null && ge.getStatus() == STATUS_CLOSE) {
			ge.setStatus(STATUS_START);
			ge.setUpdateTime(new Date());
			gatewayDao.updateStatus(ge);

			createGatewayKey(ge);
		}
	}

	public void closeGateway(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "gateway id is invalid");
		}
		GatewayEntity ge = gatewayDao.find(id);
		if (ge != null && ge.getStatus() == STATUS_START) {
			ge.setStatus(STATUS_CLOSE);
			ge.setUpdateTime(new Date());
			gatewayDao.updateStatus(ge);

			deleteGatewayKey(ge);
		}
	}

	public List<RouteDefinition> routeList(Integer appId) {
		List<RouteEntity> aes = null;
		if (appId != null) {
			aes = routeDao.list(appId);
		}
		if (aes != null) {
			return aes.stream().map(re -> convert(re)).collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	private RouteDefinition convert(RouteEntity re) {
		RouteDefinition rd = JacksonCodec.decode(re.getJson(), RouteDefinition.class);
		rd.setId(re.getId());
		rd.setName(re.getName());
		rd.setAppId(re.getAppId());
		rd.setStatus(re.getStatus());
		return rd;
	}

	private RouteEntity convert(RouteDefinition defination) {
		RouteEntity entity = new RouteEntity();
		entity.setId(defination.getId());
		entity.setName(defination.getName());
		entity.setAppId(defination.getAppId());
		entity.setStatus(defination.getStatus());

		entity.setJson(JacksonCodec.encode(defination));
		return entity;
	}

	public void createRoute(RouteDefinition route) throws BusinessException {
		if (route.getAppId() == null) {
			throw new BusinessException(40200, "APP is invalid");
		}
		if (route.getName() == null || route.getName().isEmpty()) {
			throw new BusinessException(40201, "Route name is empty");
		}
		FrontendDefinition frontendDefination = route.getFrontend();
		if (frontendDefination == null) {
			throw new BusinessException(40202, "frontend defination is null");
		}
		if (frontendDefination.getPath() == null || frontendDefination.getPath().isEmpty()) {
			throw new BusinessException(40203, "request path is empty");
		}
		BackendDefinition backendDefination = route.getBackend();
		if (backendDefination == null) {
			throw new BusinessException(40204, "backend defination is null");
		}
		if (backendDefination.getUrls() == null || backendDefination.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}

		for (Iterator<String> iterator = backendDefination.getUrls().iterator(); iterator.hasNext();) {
			String next = iterator.next();
			if (next == null || next.isEmpty()) {
				iterator.remove();
			}
		}
		if (backendDefination.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}

		if (routeDao.routeNameExist(route.getName())) {
			throw new BusinessException(40206, "route name is invalid");
		}

		AppEntity appEntity = appDao.find(route.getAppId());
		if (appEntity == null) {
			throw new BusinessException(40200, "APP is invalid");
		}

		RouteEntity routeEntity = convert(route);
		Date now = new Date();
		routeEntity.setCreateTime(now);
		routeEntity.setUpdateTime(now);

		routeDao.create(routeEntity);
	}

	public void updateRoute(RouteDefinition routeDefination) throws BusinessException {
		if (routeDefination.getAppId() == null) {
			throw new BusinessException(40200, "APP is invalid");
		}
		if (routeDefination.getName() == null || routeDefination.getName().isEmpty()) {
			throw new BusinessException(40201, "Route name is empty");
		}
		FrontendDefinition frontend = routeDefination.getFrontend();
		if (frontend == null) {
			throw new BusinessException(40202, "frontend config is null");
		}
		if (frontend.getPath() == null || frontend.getPath().isEmpty()) {
			throw new BusinessException(40203, "frontend path is empty");
		}
		BackendDefinition backend = routeDefination.getBackend();
		if (backend == null) {
			throw new BusinessException(40204, "backend config is null");
		}
		if (backend.getUrls() == null || backend.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}
		for (Iterator<String> iterator = backend.getUrls().iterator(); iterator.hasNext();) {
			String next = iterator.next();
			if (next == null || next.isEmpty()) {
				iterator.remove();
			}
		}
		if (backend.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}

		RouteEntity routeEntity = routeDao.find(routeDefination.getId());
		if (routeEntity == null) {
			throw new BusinessException(40207, "can't find route");
		}
		AppEntity appEntity = appDao.find(routeDefination.getAppId());
		if (appEntity == null) {
			throw new BusinessException(40208, "can't find app");
		}

		routeEntity = convert(routeDefination);
		routeEntity.setUpdateTime(new Date());

		routeDao.update(routeEntity);
	}

	public RouteDefinition getRouteById(Integer id) {
		RouteEntity ae = routeDao.find(id);
		if (ae != null) {
			return convert(ae);
		}
		return null;
	}

	public void deleteRoute(Integer id) {
		closeRoute(id);
		routeDao.delete(id);
	}

	public void startRoute(Integer id) throws BusinessException {
		RouteEntity routeEntity = routeDao.find(id);
		if (routeEntity != null && routeEntity.getStatus() == STATUS_CLOSE) {
			routeEntity.setStatus(STATUS_START);
			routeEntity.setUpdateTime(new Date());
			routeDao.updateStatus(routeEntity);

			AppEntity appEntity = appDao.find(routeEntity.getAppId());
			GatewayEntity gatewayEntity = gatewayDao.find(appEntity.getGwId());
			createRouteKey(gatewayEntity, routeEntity);
		}
	}

	private String convertRouteOptions(GatewayEntity gatewayEntity, RouteEntity routeEntity) {
		AppDefinition appDefination = getAppById(routeEntity.getAppId());
		RouteDefinition routeDefination = convert(routeEntity);

		Map<String, Object> routeOptions = new HashMap<>();
		routeOptions.put("cluster", gatewayEntity.getCluster());
		routeOptions.put("gateway", gatewayEntity.getName());
		routeOptions.put("appNmae", appDefination.getName());
		routeOptions.put("prefix", appDefination.getPrefix());
		routeOptions.put("domain", appDefination.getDomain());
		routeOptions.put("route", routeDefination.getName());

		List<PluginDefinition> plugins = routeDefination.getPlugins();
		List<Map<String, Object>> pluginOptions = new LinkedList<>();
		if (plugins != null) {
			plugins.forEach(plugin -> {
				Map<String, Object> pluginOption = new HashMap<>();
				pluginOption.put("type", plugin.getType());
				pluginOption.put("order", plugin.getOrder());
				pluginOption.put("plugin", plugin.getPlugin());
				pluginOption.put("options", json2map(plugin.getConfig()));
				pluginOptions.add(pluginOption);
			});
		}
		routeOptions.put("plugins", pluginOptions);

		FrontendDefinition frontend = routeDefination.getFrontend();
		Map<String, Object> fOptions = new HashMap<>();
		fOptions.put("path", frontend.getPath());
		fOptions.put("method", frontend.getMethod());
		fOptions.put("produces", toArray(frontend.getProduces()));
		fOptions.put("consumes", toArray(frontend.getConsumes()));
		routeOptions.put("frontend", fOptions);

		BackendDefinition backend = routeDefination.getBackend();
		Map<String, Object> bOptions = new HashMap<>();
		bOptions.put("type", backend.getType());
		bOptions.put("timeout", backend.getTimeout());
		bOptions.put("urls", backend.getUrls());
		bOptions.put("path", backend.getPath());
		bOptions.put("method", backend.getMethod());
		bOptions.put("algorithm", backend.getAlgorithm());
		bOptions.put("registry", json2map(backend.getRegistry()));
		bOptions.put("connection", json2map(backend.getConnection()));
		bOptions.put("params", backend.getParams());
		routeOptions.put("backend", bOptions);

		return JacksonCodec.encode(routeOptions);
	}

	private String[] toArray(String source) {
		if (source != null) {
			return source.split(",");
		}
		return null;
	}

	public void closeRoute(Integer id) {
		RouteEntity route = routeDao.find(id);
		if (route != null && route.getStatus() == STATUS_START) {
			route.setStatus(STATUS_CLOSE);
			routeDao.updateStatus(route);

			AppEntity ae = appDao.find(route.getAppId());
			GatewayEntity ge = gatewayDao.find(ae.getGwId());

			deleteRouteKey(ge, route);
		}
	}

	public void gatewayRefresh() {
		// clear deleted gateway
		String key = "agate/gateway";
		List<String> ks = catalogStore.getKeys(key);
		for (String k : ks) {
			Optional<String> v = catalogStore.getValue(k);
			if (v.isPresent()) {
				String[] gps = k.split("/");
				GatewayEntity g = gatewayDao.find(gps[2], gps[3]);
				if (g == null || g.getStatus() == STATUS_CLOSE) {
					catalogStore.deleteKey(k);

					key = "agate/route/" + gps[2] + "/" + gps[3];
					catalogStore.deleteKey(key);
				}
			}
		}

		// iterator gateway
		List<GatewayEntity> ges = gatewayDao.list();
		for (GatewayEntity gateway : ges) {
			if (gateway.getStatus() == STATUS_START) {
				if (!existGatewayKey(gateway)) {
					createGatewayKey(gateway);
				}

				routeRefresh(gateway);
			} else {
				if (existGatewayKey(gateway)) {
					deleteGatewayKey(gateway);
				}
			}
		}
	}

	private void routeRefresh(GatewayEntity gateway) {
		// refresh routes status
		List<RouteEntity> routes = routeDao.listByGatewayId(gateway.getId());
		for (RouteEntity route : routes) {
			if (route.getStatus() == STATUS_START) {
				if (!existRouteKey(gateway, route)) {
					createRouteKey(gateway, route);
				}
			} else {
				if (existRouteKey(gateway, route)) {
					deleteRouteKey(gateway, route);
				}
			}
		}
	}

	private String routeKey(GatewayEntity ge, RouteEntity re) {
		return "agate/route/" + ge.getCluster() + "/" + ge.getName() + "/" + re.getName();
	}

	private boolean existRouteKey(GatewayEntity ge, RouteEntity re) {
		String key = routeKey(ge, re);
		return catalogStore.getValue(key).isPresent();
	}

	private void createRouteKey(GatewayEntity ge, RouteEntity re) {
		String key = routeKey(ge, re);
		String value = convertRouteOptions(ge, re);
		catalogStore.putValue(key, value);
	}

	private void deleteRouteKey(GatewayEntity ge, RouteEntity re) {
		String key = routeKey(ge, re);
		catalogStore.deleteKey(key);
	}

	private String convertGatewayOptions(GatewayEntity entity) {
		GatewayDefinition defination = convert(entity);
		Map<String, Object> gatewayOptions = new HashMap<>();
		gatewayOptions.put("cluster", defination.getCluster());
		gatewayOptions.put("gateway", defination.getName());
		gatewayOptions.put("remark", defination.getRemark());

		Map<String, Object> serverOptions = json2map(defination.getServerConfig());
		serverOptions.put("host", defination.getHost());
		serverOptions.put("port", defination.getPort());
		gatewayOptions.put("serverOptions", serverOptions);

		Map<String, Object> clientOptions = json2map(defination.getClientConfig());
		gatewayOptions.put("clientOptions", clientOptions);

		return JacksonCodec.encode(gatewayOptions);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> json2map(String json) {
		Map<String, Object> options = new HashMap<>();
		if (json != null && !json.isEmpty()) {
			Map<String, Object> configs = JacksonCodec.decode(json, Map.class);
			if (configs != null) {
				options.putAll(configs);
			}
		}
		return options;
	}

	private String gatewayKey(GatewayEntity ge) {
		return "agate/gateway/" + ge.getCluster() + "/" + ge.getName();
	}

	private boolean existGatewayKey(GatewayEntity ge) {
		String key = gatewayKey(ge);
		Optional<String> value = catalogStore.getValue(key);
		return value.isPresent();
	}

	private void createGatewayKey(GatewayEntity ge) {
		String key = gatewayKey(ge);
		String value = convertGatewayOptions(ge);
		catalogStore.putValue(key, value);
	}

	private void deleteGatewayKey(GatewayEntity ge) {
		String key = gatewayKey(ge);
		catalogStore.deleteKey(key);
	}

	public List<AppDefinition> appList() {
		List<AppEntity> es = appDao.list();
		if (es == null || es.isEmpty()) {
			return Collections.emptyList();
		} else {
			return es.stream().map(ae -> {
				AppDefinition app = JacksonCodec.decode(ae.getJson(), AppDefinition.class);
				app.setId(ae.getId());
				return app;
			}).collect(Collectors.toList());
		}
	}

	public AppDefinition getAppById(Integer id) {
		AppEntity ae = appDao.find(id);
		if (ae != null) {
			AppDefinition app = JacksonCodec.decode(ae.getJson(), AppDefinition.class);
			app.setId(ae.getId());
			return app;
		}
		return null;
	}

	public void createApp(AppDefinition defination) throws BusinessException {
		if (defination.getName() == null || defination.getName().isEmpty()) {
			throw new BusinessException(40301, "APP Name is empty");
		}
		// app logic check
		if (appDao.appNameExist(defination.getName())) {
			throw new BusinessException(40107, "APP Name is not unique");
		}

		AppEntity ae = new AppEntity();
		ae.setGwId(defination.getGwId());
		ae.setName(defination.getName());
		Date createTime = new Date();
		ae.setCreateTime(createTime);
		ae.setUpdateTime(createTime);
		ae.setJson(JacksonCodec.encode(defination));
		appDao.create(ae);
	}

	public void updateApp(AppDefinition defination) throws BusinessException {
		if (defination.getName() == null || defination.getName().isEmpty()) {
			throw new BusinessException(40301, "APP Name is empty");
		}

		AppEntity ae = new AppEntity();
		ae.setId(defination.getId());
		ae.setGwId(defination.getGwId());
		ae.setName(defination.getName());
		Date createTime = new Date();
		ae.setUpdateTime(createTime);
		ae.setJson(JacksonCodec.encode(defination));
		appDao.update(ae);
	}

	public void deleteApp(Integer id) throws BusinessException {
		appDao.delete(id);
	}

}
