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
			e.printStackTrace();
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
