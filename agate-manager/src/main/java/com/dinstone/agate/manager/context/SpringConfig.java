package com.dinstone.agate.manager.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.Consul.Builder;
import com.orbitz.consul.KeyValueClient;

@Configuration
//@PropertySource(value = { "classpath:jdbc.properties" })
//@ComponentScan(basePackages = { "com.dinstone.agate.manager.service", "com.dinstone.agate.manager.dao" })
public class SpringConfig {

	@Autowired
	private Environment environment;

	@Bean
	KeyValueClient consulClient() {
		String host = environment.getProperty("consul.host");
		String port = environment.getProperty("consul.port");

		Builder builder = Consul.builder();
		if (host != null && port != null) {
			HostAndPort hostAndPort = HostAndPort.fromParts(host, Integer.parseInt(port));
			builder.withHostAndPort(hostAndPort);
		}
		return builder.build().keyValueClient();
	}
}
