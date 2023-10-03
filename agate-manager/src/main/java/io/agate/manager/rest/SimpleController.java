package io.agate.manager.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SimpleController {

	@GetMapping("/access")
	public String access() {
//		throw new RuntimeException("ControllerException@SimpleController");
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foo param is error", null);
	}

}
