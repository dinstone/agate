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
package io.agate.admin.store;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.CatalogServiceRequest;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.ecwid.consul.v1.kv.model.GetValue;
import io.agate.admin.business.port.CatalogStore;

import java.util.*;
import java.util.stream.Collectors;

public class ConsulCatalogStore implements CatalogStore {

    private final ConsulClient consulClient;

    public ConsulCatalogStore(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @Override
    public List<Map<String, String>> getMetas(String service) {
        CatalogServiceRequest request = CatalogServiceRequest.newBuilder().build();
        Response<List<CatalogService>> consulResponse = consulClient.getCatalogService(service, request);

        List<Map<String, String>> instances = new LinkedList<>();
        for (CatalogService cs : consulResponse.getValue()) {
            Map<String, String> instance = cs.getServiceMeta();
            if (instance != null) {
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    public void deleteKey(String key) {
        consulClient.deleteKVValue(key);
    }

    @Override
    public List<String> getKeys(String key) {
        Response<List<GetValue>> rv = consulClient.getKVValues(key);
        if (rv.getValue() == null) {
            return Collections.emptyList();
        }
        return rv.getValue().stream().map(GetValue::getValue).collect(Collectors.toList());
    }

    @Override
    public Optional<String> getValue(String k) {
        Response<GetValue> rv = consulClient.getKVValue(k);
        return Optional.of(rv.getValue().getValue());
    }

    @Override
    public void putValue(String key, String value) {
        consulClient.setKVValue(key, value);
    }

}
