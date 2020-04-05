package com.dinstone.agate.manager;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
@PropertySource(value = { "classpath:jdbc.properties" })
public class SpringJdbcTest {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		AnnotationConfigApplicationContext sac = new AnnotationConfigApplicationContext(SpringJdbcTest.class);
		DataSource ds = (DataSource) sac.getBean("dataSource");
		try {
			ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		sac.close();
	}

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		String un = environment.getProperty("username");
		System.out.println(un);

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//		dataSource.setDriverClass("org.sqlite.JDBC");
		dataSource.setUrl("jdbc:sqlite:config.db");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}
}
