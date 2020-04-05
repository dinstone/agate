package com.dinstone.agate.manager.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenResource {

	@GetMapping("hi")
	public String hi() {
		return "hello world";
	}

}
