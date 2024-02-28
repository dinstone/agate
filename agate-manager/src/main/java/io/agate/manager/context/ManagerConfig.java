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
package io.agate.manager.context;

import com.ecwid.consul.v1.ConsulClient;
import io.agate.admin.business.port.CatalogStore;
import io.agate.admin.store.ConsulCatalogStore;
import io.agate.admin.store.EmptyCatalogStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("io.agate.admin")
public class ManagerConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ManagerConfig.class);

	@Autowired
	private Environment environment;

	ConsulClient consul() {
		String host = environment.getProperty("consul.host");
		String port = environment.getProperty("consul.port");

		return new ConsulClient(host, Integer.parseInt(port));
	}

	@Bean
	CatalogStore catalogStore() {
		try {
			return new ConsulCatalogStore(consul());
		} catch (Exception e) {
			LOG.warn("create consul catalog store error [{}], will use empty catalog store.", e.getMessage());
		}
		return new EmptyCatalogStore();
	}

}
