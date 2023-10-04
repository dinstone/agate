package io.agate.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgateAdminApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(AgateAdminApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
