/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.dao.ApiRouteDao;
import com.dinstone.agate.manager.dao.GatewayDao;
import com.dinstone.agate.manager.model.ApiConfig;
import com.dinstone.agate.manager.model.ApiRouteEntity;
import com.dinstone.agate.manager.model.GatewayEntity;
import com.dinstone.agate.manager.model.HandlersConfig;
import com.dinstone.agate.manager.model.ParamConfig;
import com.dinstone.agate.manager.model.RequestConfig;
import com.dinstone.agate.manager.model.ResponseConfig;
import com.dinstone.agate.manager.model.RoutingConfig;
import com.dinstone.agate.manager.utils.JacksonCodec;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;

@Component
public class ManageService {

    private static final int STATUS_START = 1;
    private static final int STATUS_CLOSE = 0;

    @Autowired
    private GatewayDao gatewayDao;

    @Autowired
    private ApiRouteDao apiRouteDao;

    @Autowired
    private KeyValueClient keyValueClient;

    public void createGateway(GatewayEntity entity) throws BusinessException {
        // app param check
        gatewayParamCheck(entity);

        Date now = new Date();

        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        gatewayDao.create(entity);
    }

    private void gatewayParamCheck(GatewayEntity entity) throws BusinessException {
        if (entity.getName() == null || entity.getName().isEmpty()) {
            throw new BusinessException(40101, "Gateway Name is empty");
        }
        if (entity.getCluster() == null || entity.getCluster().isEmpty()) {
            throw new BusinessException(40102, "Cluster is empty");
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
        if (gatewayDao.gatewayNameExist(entity)) {
            throw new BusinessException(40107, "Gateway Name is not unique for cluster");
        }
        if (gatewayDao.gatewayPortExist(entity)) {
            throw new BusinessException(40107, "Gateway Port is not unique for cluster");
        }
    }

    public void updateGateway(GatewayEntity entity) throws BusinessException {
        // app logic check
        if (entity.getId() == null) {
            throw new BusinessException(40108, "Gateway id is invalid");
        }

        // app param check
        gatewayParamCheck(entity);

        GatewayEntity ue = gatewayDao.find(entity.getId());
        if (ue == null) {
            throw new BusinessException(40109, "can't find gateway");
        }

        ue.setUpdateTime(new Date());
        ue.setName(entity.getName());
        ue.setCluster(entity.getCluster());
        ue.setHost(entity.getHost());
        ue.setPort(entity.getPort());
        ue.setRemark(entity.getRemark());
        ue.setServerConfig(entity.getServerConfig());
        ue.setClientConfig(entity.getClientConfig());

        gatewayDao.update(ue);
    }

    private boolean checkJsonFormat(String config) throws BusinessException {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<GatewayEntity> gatewayList() {
        return gatewayDao.list();
    }

    public GatewayEntity getGatewayById(Integer id) {
        if (id == null) {
            return null;
        }
        return gatewayDao.find(id);
    }

    public void deleteGateway(Integer gwId) throws BusinessException {
        if (gwId == null) {
            throw new BusinessException(40108, "Gateway id is invalid");
        }

        // close APIs
        List<ApiRouteEntity> ael = apiRouteDao.list(gwId);
        if (ael != null) {
            for (ApiRouteEntity entity : ael) {
                closeApi(entity.getArId());
            }
        }

        // close Gateway
        closeGateway(gwId);

        // delete APIs and Gateway
        apiRouteDao.deleteByGatewayId(gwId);
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
            throw new BusinessException(40108, "APP id is invalid");
        }
        GatewayEntity ge = gatewayDao.find(id);
        if (ge != null && ge.getStatus() == STATUS_START) {
            ge.setStatus(STATUS_CLOSE);
            ge.setUpdateTime(new Date());
            gatewayDao.updateStatus(ge);

            deleteGatewayKey(ge);
        }
    }

    public List<ApiConfig> apiList() {
        List<ApiConfig> apiConfigs = new LinkedList<>();

        List<ApiRouteEntity> aes = apiRouteDao.list();
        if (aes != null) {
            for (ApiRouteEntity apiEntity : aes) {
                apiConfigs.add(covert(apiEntity));
            }
        }

        return apiConfigs;
    }

    private ApiConfig covert(ApiRouteEntity apiEntity) {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setArId(apiEntity.getArId());
        apiConfig.setGwId(apiEntity.getGwId());
        apiConfig.setName(apiEntity.getName());
        apiConfig.setRemark(apiEntity.getRemark());
        apiConfig.setStatus(apiEntity.getStatus());

        GatewayEntity ge = gatewayDao.find(apiEntity.getGwId());
        apiConfig.setGateway(ge.getName() + "(" + ge.getPort() + ")");

        RoutingConfig bc = JacksonCodec.decode(apiEntity.getRouting(), RoutingConfig.class);
        apiConfig.setRoutingConfig(bc);

        RequestConfig fc = JacksonCodec.decode(apiEntity.getRequest(), RequestConfig.class);
        apiConfig.setRequestConfig(fc);

        ResponseConfig rc = JacksonCodec.decode(apiEntity.getResponse(), ResponseConfig.class);
        apiConfig.setResponseConfig(rc);

        HandlersConfig hc = JacksonCodec.decode(apiEntity.getHandlers(), HandlersConfig.class);
        apiConfig.setHandlersConfig(hc);

        return apiConfig;
    }

    private ApiRouteEntity covert(ApiConfig apiConfig) {
        ApiRouteEntity apiEntity = new ApiRouteEntity();
        apiEntity.setArId(apiConfig.getArId());
        apiEntity.setGwId(apiConfig.getGwId());
        apiEntity.setName(apiConfig.getName());
        apiEntity.setRemark(apiConfig.getRemark());

        apiEntity.setRequest(JacksonCodec.encode(apiConfig.getRequestConfig()));
        apiEntity.setResponse(JacksonCodec.encode(apiConfig.getResponseConfig()));

        apiEntity.setHandlers(JacksonCodec.encode(apiConfig.getHandlersConfig()));

        RoutingConfig backendConfig = apiConfig.getRoutingConfig();
        if (backendConfig.getParams() != null) {
            for (Iterator<ParamConfig> iterator = backendConfig.getParams().iterator(); iterator.hasNext();) {
                ParamConfig pc = iterator.next();
                if (pc == null || pc.getFeParamName() == null || pc.getBeParamName() == null) {
                    iterator.remove();
                }
            }
        }
        apiEntity.setRouting(JacksonCodec.encode(backendConfig));

        return apiEntity;
    }

    public void createApi(ApiConfig apiConfig) throws BusinessException {
        if (apiConfig.getGwId() == null) {
            throw new BusinessException(40200, "gateway is invalid");
        }
        if (apiConfig.getName() == null || apiConfig.getName().isEmpty()) {
            throw new BusinessException(40201, "API name is empty");
        }
        RequestConfig frontendConfig = apiConfig.getRequestConfig();
        if (frontendConfig == null) {
            throw new BusinessException(40202, "request config is null");
        }
        if (frontendConfig.getPath() == null || frontendConfig.getPath().isEmpty()) {
            throw new BusinessException(40203, "request path is empty");
        }
        RoutingConfig backendConfig = apiConfig.getRoutingConfig();
        if (backendConfig == null) {
            throw new BusinessException(40204, "routing config is null");
        }
        if (backendConfig.getUrls() == null || backendConfig.getUrls().isEmpty()) {
            throw new BusinessException(40205, "routing urls is null");
        }

        for (Iterator<String> iterator = backendConfig.getUrls().iterator(); iterator.hasNext();) {
            String next = iterator.next();
            if (next == null || next.isEmpty()) {
                iterator.remove();
            }
        }
        if (backendConfig.getUrls().isEmpty()) {
            throw new BusinessException(40205, "routing urls is null");
        }

        if (apiRouteDao.apiNameExist(apiConfig.getName())) {
            throw new BusinessException(40206, "API name is invalid");
        }

        GatewayEntity entity = gatewayDao.find(apiConfig.getGwId());
        if (entity == null) {
            return;
        }

        ApiRouteEntity apiEntity = covert(apiConfig);
        Date now = new Date();
        apiEntity.setCreateTime(now);
        apiEntity.setUpdateTime(now);

        apiRouteDao.create(apiEntity);
    }

    public void updateApi(ApiConfig apiConfig) throws BusinessException {
        RequestConfig frontendConfig = apiConfig.getRequestConfig();
        if (frontendConfig == null) {
            throw new BusinessException(40202, "frontend config is null");
        }
        if (frontendConfig.getPath() == null || frontendConfig.getPath().isEmpty()) {
            throw new BusinessException(40203, "frontend path is empty");
        }
        RoutingConfig backendConfig = apiConfig.getRoutingConfig();
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

        ApiRouteEntity apiEntity = apiRouteDao.find(apiConfig.getArId());
        if (apiEntity == null) {
            throw new BusinessException(40207, "can't find API");
        }
        GatewayEntity appEntity = gatewayDao.find(apiEntity.getGwId());
        if (appEntity == null) {
            throw new BusinessException(40208, "can't find Gateway");
        }

        ApiRouteEntity ae = covert(apiConfig);
        apiEntity.setRemark(ae.getRemark());
        apiEntity.setRequest(ae.getRequest());
        apiEntity.setRouting(ae.getRouting());
        apiEntity.setResponse(ae.getResponse());
        apiEntity.setHandlers(ae.getHandlers());
        apiEntity.setUpdateTime(new Date());

        apiRouteDao.update(apiEntity);
    }

    public ApiConfig getApiById(Integer apiId) {
        ApiRouteEntity ae = apiRouteDao.find(apiId);
        if (ae != null) {
            return covert(ae);
        }
        return null;
    }

    public void deleteApi(Integer apiId) {
        closeApi(apiId);
        apiRouteDao.delete(apiId);
    }

    public void startApi(Integer arId) throws BusinessException {
        ApiRouteEntity api = apiRouteDao.find(arId);
        if (api != null && api.getStatus() == STATUS_CLOSE) {
            GatewayEntity ge = gatewayDao.find(api.getGwId());
            if (ge.getStatus() != STATUS_START) {
                throw new BusinessException(40290, "gateway is closed");
            }

            api.setStatus(STATUS_START);
            apiRouteDao.updateStatus(api);

            String key = "agate/apis/" + ge.getName() + "/" + api.getName();
            String value = convertApiOption(ge, api);
            keyValueClient.putValue(key, value);
        }
    }

    private String convertApiOption(GatewayEntity gatewayEntity, ApiRouteEntity apiEntity) {
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put("cluster", gatewayEntity.getCluster());
        apiOptions.put("gateway", gatewayEntity.getName());
        apiOptions.put("apiName", apiEntity.getName());

        apiOptions.put("request", JacksonCodec.decode(apiEntity.getRequest(), Map.class));
        apiOptions.put("routing", JacksonCodec.decode(apiEntity.getRouting(), Map.class));
        apiOptions.put("response", JacksonCodec.decode(apiEntity.getResponse(), Map.class));
        apiOptions.put("handlers", JacksonCodec.decode(apiEntity.getHandlers(), Map.class));

        return JacksonCodec.encode(apiOptions);
    }

    public void closeApi(Integer apiId) {
        ApiRouteEntity api = apiRouteDao.find(apiId);
        if (api != null && api.getStatus() == STATUS_START) {
            api.setStatus(STATUS_CLOSE);
            apiRouteDao.updateStatus(api);

            GatewayEntity ge = gatewayDao.find(api.getGwId());

            String key = "agate/apis/" + ge.getName() + "/" + api.getName();
            keyValueClient.deleteKey(key);
        }
    }

    public void gatewayRefresh() {
        String key = "agate/gateway";
        List<String> ks = keyValueClient.getKeys(key);
        for (String k : ks) {
            Optional<Value> v = keyValueClient.getValue(k);
            if (v.isPresent()) {
                String[] gps = v.get().getKey().split("/");
                GatewayEntity g = gatewayDao.find(gps[2], gps[3]);
                if (g == null || g.getStatus() == STATUS_CLOSE) {
                    keyValueClient.deleteKey(k);

                    key = "agate/apis/" + gps[3];
                    keyValueClient.deleteKey(key);
                }
            }
        }

        List<GatewayEntity> ges = gatewayDao.all();
        for (GatewayEntity ge : ges) {
            if (ge.getStatus() == STATUS_START) {
                if (!existGatewayKey(ge)) {
                    createGatewayKey(ge);
                }
            } else {
                deleteGatewayKey(ge);
            }
        }
    }

    private void createGatewayKey(GatewayEntity ge) {
        String key = "agate/gateway/" + ge.getCluster() + "/" + ge.getName();
        String value = convertGatewayOptions(ge);
        keyValueClient.putValue(key, value);
    }

    @SuppressWarnings("unchecked")
    private String convertGatewayOptions(GatewayEntity entity) {
        Map<String, Object> appOptions = new HashMap<>();
        appOptions.put("cluster", entity.getCluster());
        appOptions.put("gateway", entity.getName());
        appOptions.put("remark", entity.getRemark());

        Map<String, Object> serverOptions = null;
        if (entity.getServerConfig() != null && !entity.getServerConfig().isEmpty()) {
            serverOptions = JacksonCodec.decode(entity.getServerConfig(), Map.class);
        } else {
            serverOptions = new HashMap<>();
        }
        serverOptions.put("host", entity.getHost());
        serverOptions.put("port", entity.getPort());
        appOptions.put("serverOptions", serverOptions);

        if (entity.getClientConfig() != null && !entity.getClientConfig().isEmpty()) {
            Map<String, Object> clientOptions = JacksonCodec.decode(entity.getClientConfig(), Map.class);
            appOptions.put("clientOptions", clientOptions);
        }

        return JacksonCodec.encode(appOptions);
    }

    private boolean existGatewayKey(GatewayEntity ge) {
        String key = "agate/gateway/" + ge.getCluster() + "/" + ge.getName();
        Optional<Value> value = keyValueClient.getValue(key);
        return !value.isPresent();
    }

    private void deleteGatewayKey(GatewayEntity ge) {
        String key = "agate/gateway/" + ge.getCluster() + "/" + ge.getName();
        keyValueClient.deleteKey(key);
    }

}
