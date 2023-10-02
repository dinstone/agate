package io.agate.domain.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.agate.domain.port.CatalogStore;

public class EmptyCatalogStore implements CatalogStore {

	@Override
	public void deleteKey(String key) {

	}

	@Override
	public List<String> getKeys(String key) {
		return Collections.emptyList();
	}

	@Override
	public Optional<String> getValue(String k) {
		return Optional.empty();
	}

	@Override
	public void putValue(String key, String value) {
	}

	@Override
	public List<Map<String, String>> getMetas(String service) {
		return Collections.emptyList();
	}

}
