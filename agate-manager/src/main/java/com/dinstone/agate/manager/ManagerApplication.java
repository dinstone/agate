package com.dinstone.agate.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagerApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(ManagerApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
