package com.dinstone.agate.manager.context;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

//@Configuration
//@PropertySource(value = { "classpath:jdbc.properties" })
//@ComponentScan(basePackages = { "com.dinstone.agate.manager.service", "com.dinstone.agate.manager.dao" })
public class SpringConfig {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(environment.getProperty("driver"));
		dataSource.setJdbcUrl(environment.getProperty("url"));
		dataSource.setUsername(environment.getProperty("username"));
		dataSource.setPassword(environment.getProperty("password"));
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
