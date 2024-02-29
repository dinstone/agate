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
package io.agate.admin.business.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.AppDefinition;
import io.agate.admin.business.model.BackendDefinition;
import io.agate.admin.business.model.FrontendDefinition;
import io.agate.admin.business.model.GatewayDefinition;
import io.agate.admin.business.model.PluginDefinition;
import io.agate.admin.business.model.RouteDefinition;
import io.agate.admin.business.param.GatewayQuery;
import io.agate.admin.business.param.PageList;
import io.agate.admin.business.param.PageQuery;
import io.agate.admin.business.param.RouteQuery;
import io.agate.admin.business.port.AppRepository;
import io.agate.admin.business.port.CatalogStore;
import io.agate.admin.business.port.GatewayRepository;
import io.agate.admin.business.port.RouteRepository;
import io.agate.admin.utils.JacksonCodec;

@Component
public class ManageService {

	private static final int STATUS_START = 1;

	private static final int STATUS_CLOSE = 0;

	@Autowired
	private GatewayRepository gatewayRepository;

	@Autowired
	private RouteRepository routeRepository;

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private CatalogStore catalogStore;

	public void createGateway(GatewayDefinition definition) throws BusinessException {
		// app param check
		gatewayParamCheck(definition);

		gatewayRepository.create(definition);
	}

	private void gatewayParamCheck(GatewayDefinition definition) throws BusinessException {
		if (definition.getName() == null || definition.getName().isEmpty()) {
			throw new BusinessException(40101, "Gateway Name is empty");
		}
		if (definition.getCcode() == null) {
			throw new BusinessException(40102, "Cluster is empty");
		}
		if (definition.getPort() == null || definition.getPort() <= 0) {
			throw new BusinessException(40104, "Port must be great than 0");
		}
		// app logic check
		if (gatewayRepository.gatewayNameExist(definition)) {
			throw new BusinessException(40107, "Gateway Name is not unique for cluster");
		}
	}

	public void updateGateway(GatewayDefinition definition) throws BusinessException {
		// app logic check
		if (definition.getId() == null) {
			throw new BusinessException(40108, "Gateway id is invalid");
		}

		// app param check
		gatewayParamCheck(definition);

		gatewayRepository.update(definition);
	}

	public List<GatewayDefinition> gatewayList() {
		return gatewayRepository.list();
	}

	public GatewayDefinition getGatewayById(Integer id) {
		return gatewayRepository.find(id);
	}

	public void deleteGateway(Integer gwId) throws BusinessException {
		// close Gateway
		closeGateway(gwId);

		// close Routes
		List<RouteDefinition> rel = routeRepository.list(gwId);
		if (rel != null) {
			for (RouteDefinition definition : rel) {
				closeRoute(definition.getId());
			}
		}

		// delete Gateway
		gatewayRepository.delete(gwId);
	}

	public void startGateway(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "Gateway id is invalid");
		}
		GatewayDefinition gd = gatewayRepository.find(id);
		if (gd != null && gd.getStatus() == STATUS_CLOSE) {
			gd.setStatus(STATUS_START);
			gatewayRepository.updateStatus(gd);

			createGatewayKey(gd);
		}
	}

	public void closeGateway(Integer id) throws BusinessException {
		if (id == null) {
			throw new BusinessException(40108, "gateway id is invalid");
		}
		GatewayDefinition gd = gatewayRepository.find(id);
		if (gd != null && gd.getStatus() == STATUS_START) {
			gd.setStatus(STATUS_CLOSE);
			gatewayRepository.updateStatus(gd);

			deleteGatewayKey(gd);
		}
	}

	public List<RouteDefinition> routeList(Integer appId) {
		return routeRepository.list(appId);
	}

	public void createRoute(RouteDefinition route) throws BusinessException {
		if (route.getAppId() == null) {
			throw new BusinessException(40200, "APP is invalid");
		}
		if (route.getName() == null || route.getName().isEmpty()) {
			throw new BusinessException(40201, "Route name is empty");
		}
		FrontendDefinition frontendDefinition = route.getFrontend();
		if (frontendDefinition == null) {
			throw new BusinessException(40202, "frontend definition is null");
		}
		if (frontendDefinition.getPath() == null || frontendDefinition.getPath().isEmpty()) {
			throw new BusinessException(40203, "request path is empty");
		}
		BackendDefinition backendDefinition = route.getBackend();
		if (backendDefinition == null) {
			throw new BusinessException(40204, "backend definition is null");
		}
		if (backendDefinition.getUrls() == null || backendDefinition.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}
        backendDefinition.getUrls().removeIf(next -> next == null || next.isEmpty());
		if (backendDefinition.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}

		if (routeRepository.routeNameExist(route.getName())) {
			throw new BusinessException(40206, "route name is invalid");
		}

		AppDefinition app = appRepository.find(route.getAppId());
		if (app == null) {
			throw new BusinessException(40200, "APP is invalid");
		}

		routeRepository.create(route);
	}

	public void updateRoute(RouteDefinition definition) throws BusinessException {
		if (definition.getAppId() == null) {
			throw new BusinessException(40200, "APP is invalid");
		}
		if (definition.getName() == null || definition.getName().isEmpty()) {
			throw new BusinessException(40201, "Route name is empty");
		}
		FrontendDefinition frontend = definition.getFrontend();
		if (frontend == null) {
			throw new BusinessException(40202, "frontend config is null");
		}
		if (frontend.getPath() == null || frontend.getPath().isEmpty()) {
			throw new BusinessException(40203, "frontend path is empty");
		}
		BackendDefinition backend = definition.getBackend();
		if (backend == null) {
			throw new BusinessException(40204, "backend config is null");
		}
		if (backend.getUrls() == null || backend.getUrls().isEmpty()) {
			throw new BusinessException(40205, "backend urls is null");
		}
        backend.getUrls().removeIf(next -> next == null || next.isEmpty());
		if (backend.getUrls().isEmpty()) {
			throw new BusinessException(40205, "routing urls is null");
		}

		RouteDefinition route = routeRepository.find(definition.getId());
		if (route == null) {
			throw new BusinessException(40207, "can't find route");
		}
		AppDefinition app = appRepository.find(definition.getAppId());
		if (app == null) {
			throw new BusinessException(40208, "can't find app");
		}

		routeRepository.update(definition);
	}

	public RouteDefinition getRouteById(Integer id) {
		return routeRepository.find(id);
	}

	public void deleteRoute(Integer id) {
		closeRoute(id);
		routeRepository.delete(id);
	}

	public void startRoute(Integer id) throws BusinessException {
		RouteDefinition routeDefinition = routeRepository.find(id);
		if (routeDefinition != null && routeDefinition.getStatus() == STATUS_CLOSE) {
			routeDefinition.setStatus(STATUS_START);
			routeRepository.updateStatus(routeDefinition);

			AppDefinition app = appRepository.find(routeDefinition.getAppId());
			GatewayDefinition gatewayDefinition = gatewayRepository.find(app.getGwId());
			createRouteKey(gatewayDefinition, routeDefinition);
		}
	}

	private String convertRouteOptions(GatewayDefinition GatewayDefinition, RouteDefinition routeDefinition) {
		AppDefinition appDefinition = getAppById(routeDefinition.getAppId());

		Map<String, Object> routeOptions = new HashMap<>();
		routeOptions.put("cluster", GatewayDefinition.getCcode());
		routeOptions.put("gateway", GatewayDefinition.getName());
		routeOptions.put("appName", appDefinition.getName());
		routeOptions.put("prefix", appDefinition.getPrefix());
		routeOptions.put("domain", appDefinition.getDomain());
		routeOptions.put("route", routeDefinition.getName());

		List<PluginDefinition> plugins = routeDefinition.getPlugins();
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

		FrontendDefinition frontend = routeDefinition.getFrontend();
		Map<String, Object> fOptions = new HashMap<>();
		fOptions.put("path", frontend.getPath());
		fOptions.put("method", frontend.getMethod());
		fOptions.put("produces", toArray(frontend.getProduces()));
		fOptions.put("consumes", toArray(frontend.getConsumes()));
		routeOptions.put("frontend", fOptions);

		BackendDefinition backend = routeDefinition.getBackend();
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
		RouteDefinition route = routeRepository.find(id);
		if (route != null && route.getStatus() == STATUS_START) {
			route.setStatus(STATUS_CLOSE);
			routeRepository.updateStatus(route);

			AppDefinition ad = appRepository.find(route.getAppId());
			GatewayDefinition ge = gatewayRepository.find(ad.getGwId());

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
				GatewayDefinition g = gatewayRepository.find(gps[2], gps[3]);
				if (g == null || g.getStatus() == STATUS_CLOSE) {
					catalogStore.deleteKey(k);

					key = "agate/route/" + gps[2] + "/" + gps[3];
					catalogStore.deleteKey(key);
				}
			}
		}

		// iterator gateway
		List<GatewayDefinition> ges = gatewayRepository.list();
		for (GatewayDefinition gd : ges) {
			if (gd.getStatus() == STATUS_START) {
				if (!existGatewayKey(gd)) {
					createGatewayKey(gd);
				}

				routeRefresh(gd);
			} else {
				if (existGatewayKey(gd)) {
					deleteGatewayKey(gd);
				}
			}
		}
	}

	private void routeRefresh(GatewayDefinition gateway) {
		// refresh routes status
		List<RouteDefinition> routes = routeRepository.listByGatewayId(gateway.getId());
		for (RouteDefinition route : routes) {
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

	private String routeKey(GatewayDefinition gd, RouteDefinition rd) {
		return "agate/route/" + gd.getCcode() + "/" + gd.getName() + "/" + rd.getName();
	}

	private boolean existRouteKey(GatewayDefinition gd, RouteDefinition rd) {
		String key = routeKey(gd, rd);
		return catalogStore.getValue(key).isPresent();
	}

	private void createRouteKey(GatewayDefinition gatewayDefinition, RouteDefinition routeDefinition) {
		String key = routeKey(gatewayDefinition, routeDefinition);
		String value = convertRouteOptions(gatewayDefinition, routeDefinition);
		catalogStore.putValue(key, value);
	}

	private void deleteRouteKey(GatewayDefinition gd, RouteDefinition rd) {
		String key = routeKey(gd, rd);
		catalogStore.deleteKey(key);
	}

	private String convertGatewayOptions(GatewayDefinition gd) {
		Map<String, Object> gatewayOptions = new HashMap<>();
		gatewayOptions.put("cluster", gd.getCcode());
		gatewayOptions.put("gateway", gd.getName());
		gatewayOptions.put("remark", gd.getRemark());

		Map<String, Object> serverOptions = json2map(gd.getServerConfig());
		serverOptions.put("host", gd.getHost());
		serverOptions.put("port", gd.getPort());
		gatewayOptions.put("serverOptions", serverOptions);

		Map<String, Object> clientOptions = json2map(gd.getClientConfig());
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

	private String gatewayKey(GatewayDefinition gd) {
		return "agate/gateway/" + gd.getCcode() + "/" + gd.getName();
	}

	private boolean existGatewayKey(GatewayDefinition gd) {
		String key = gatewayKey(gd);
		Optional<String> value = catalogStore.getValue(key);
		return value.isPresent();
	}

	private void createGatewayKey(GatewayDefinition gd) {
		String key = gatewayKey(gd);
		String value = convertGatewayOptions(gd);
		catalogStore.putValue(key, value);
	}

	private void deleteGatewayKey(GatewayDefinition ge) {
		String key = gatewayKey(ge);
		catalogStore.deleteKey(key);
	}

	public List<AppDefinition> appList() {
		return appRepository.list();
	}

	public AppDefinition getAppById(Integer id) {
		return appRepository.find(id);
	}

	public void createApp(AppDefinition definition) throws BusinessException {
		if (definition.getName() == null || definition.getName().isEmpty()) {
			throw new BusinessException(40301, "APP Name is empty");
		}
		// app logic check
		if (appRepository.appNameExist(definition.getName())) {
			throw new BusinessException(40107, "APP Name is not unique");
		}

		appRepository.create(definition);
	}

	public void updateApp(AppDefinition definition) throws BusinessException {
		if (definition.getName() == null || definition.getName().isEmpty()) {
			throw new BusinessException(40301, "APP Name is empty");
		}

		appRepository.update(definition);
	}

	public void deleteApp(Integer id) throws BusinessException {
		appRepository.delete(id);
	}

	public PageList<GatewayDefinition> gatewayList(GatewayQuery query) {
		if (query.getPageSize() == null || query.getPageSize() <= 0) {
			List<GatewayDefinition> list = gatewayRepository.find(query.getCcode(), 0, Integer.MAX_VALUE);
			return new PageList<GatewayDefinition>(list);
		}

		int pageIndex = 1;
		if (query.getPageIndex() != null && query.getPageIndex() > 0) {
			pageIndex = query.getPageIndex();
		}

		int size = query.getPageSize();
		int start = (pageIndex - 1) * size;

		int total = gatewayRepository.total(query.getCcode());
		if (start < total) {
			List<GatewayDefinition> list = gatewayRepository.find(query.getCcode(), start, size);
			return new PageList<GatewayDefinition>(list, total);
		} else {
			return new PageList<GatewayDefinition>(total);
		}
	}

	public PageList<AppDefinition> appList(PageQuery query) {
		if (query.getPageSize() == null || query.getPageSize() <= 0) {
			List<AppDefinition> list = appRepository.find(0, Integer.MAX_VALUE);
			return new PageList<AppDefinition>(list);
		}

		int pageIndex = 1;
		if (query.getPageIndex() != null && query.getPageIndex() > 0) {
			pageIndex = query.getPageIndex();
		}

		int size = query.getPageSize();
		int start = (pageIndex - 1) * size;

		int total = appRepository.total();
		if (start < total) {
			List<AppDefinition> list = appRepository.find(start, size);
			return new PageList<AppDefinition>(list, total);
		} else {
			return new PageList<AppDefinition>(total);
		}
	}

	public PageList<RouteDefinition> routeList(RouteQuery query) {
		if (query.getPageSize() == null || query.getPageSize() <= 0) {
			List<RouteDefinition> list = routeRepository.find(query.getAppId(), 0, Integer.MAX_VALUE);
			return new PageList<RouteDefinition>(list);
		}

		int pageIndex = 1;
		if (query.getPageIndex() != null && query.getPageIndex() > 0) {
			pageIndex = query.getPageIndex();
		}

		int size = query.getPageSize();
		int start = (pageIndex - 1) * size;

		int total = routeRepository.total(query.getAppId());
		if (start < total) {
			List<RouteDefinition> list = routeRepository.find(query.getAppId(), start, size);
			return new PageList<RouteDefinition>(list, total);
		} else {
			return new PageList<RouteDefinition>(total);
		}
	}

}
