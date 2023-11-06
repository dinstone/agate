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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.agate.admin.business.port.CatalogStore;

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
