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
package io.agate.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.Consul.Builder;

import io.agate.admin.business.port.CatalogStore;
import io.agate.admin.store.ConsulCatalogStore;
import io.agate.admin.store.EmptyCatalogStore;

@SpringBootApplication
public class AgateAdminApplication {

	private static final Logger LOG = LoggerFactory.getLogger(AgateAdminApplication.class);

	public static void main(String[] args) {
		try {
			SpringApplication.run(AgateAdminApplication.class, args);
		} catch (Exception e) {
			LOG.error("application bootstrap error", e);
		}
	}

	@Autowired
	private Environment environment;

	Consul consul() {
		String host = environment.getProperty("consul.host");
		String port = environment.getProperty("consul.port");

		Builder builder = Consul.builder();
		if (host != null && port != null) {
			HostAndPort hostAndPort = HostAndPort.fromParts(host, Integer.parseInt(port));
			builder.withHostAndPort(hostAndPort);
		}
		return builder.build();
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

	@Bean
	public EnhanceErrorController basicErrorController(ErrorAttributes errorAttributes,
			ServerProperties serverProperties, ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
		return new EnhanceErrorController(errorAttributes, serverProperties.getError(),
				errorViewResolversProvider.getIfAvailable());
	}

}
