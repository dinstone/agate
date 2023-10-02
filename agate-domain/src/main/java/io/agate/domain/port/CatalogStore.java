package io.agate.domain.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CatalogStore {

	void deleteKey(String key);

	List<String> getKeys(String key);

	Optional<String> getValue(String k);

	void putValue(String key, String value);

	List<Map<String, String>> getMetas(String service);

}
