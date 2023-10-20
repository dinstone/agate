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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import io.agate.admin.business.port.CatalogStore;

public class ConsulCatalogStore implements CatalogStore {

	private CatalogClient catalogClient;

	private KeyValueClient keyValueClient;

	public ConsulCatalogStore(Consul consul) {
		catalogClient = consul.catalogClient();
		keyValueClient = consul.keyValueClient();
	}

	@Override
	public List<Map<String, String>> getMetas(String service) {
		ConsulResponse<List<CatalogService>> consulResponse = catalogClient.getService(service);

		List<Map<String, String>> instances = new LinkedList<>();
		for (CatalogService e : consulResponse.getResponse()) {
			Map<String, String> instance = e.getServiceMeta();
			if (instance != null) {
				instances.add(instance);
			}
		}
		return instances;
	}

	@Override
	public void deleteKey(String key) {
		keyValueClient.deleteKey(key);
	}

	@Override
	public List<String> getKeys(String key) {
		return keyValueClient.getKeys(key);
	}

	@Override
	public Optional<String> getValue(String k) {
		return keyValueClient.getValue(k).map(v -> v.getValue().get());
	}

	@Override
	public void putValue(String key, String value) {
		keyValueClient.putValue(key, value);
	}

}
