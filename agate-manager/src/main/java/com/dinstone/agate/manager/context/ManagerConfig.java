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
package com.dinstone.agate.manager.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.Consul.Builder;
import com.orbitz.consul.KeyValueClient;

@Configuration
// @PropertySource(value = { "classpath:jdbc.properties" })
// @ComponentScan(basePackages = { "com.dinstone.agate.manager.service", "com.dinstone.agate.manager.dao" })
public class ManagerConfig {

    @Autowired
    private Environment environment;

    @Bean
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
    KeyValueClient keyValueClient(Consul consul) {
        return consul.keyValueClient();
    }

    @Bean
    CatalogClient catalogClient(Consul consul) {
        return consul.catalogClient();
    }

}
