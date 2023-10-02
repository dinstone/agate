package io.agate.domain.adapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import io.agate.domain.port.CatalogStore;

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
