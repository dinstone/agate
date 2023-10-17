package io.agate.admin.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.UserDefinition;
import io.agate.admin.business.service.AuthenService;

@RestController
@RequestMapping("/authen")
public class AuthenResource {

	@Autowired
	private AuthenService authenService;

	@PostMapping("/login")
	public UserDefinition login(@RequestBody UserDefinition user) throws BusinessException {
		return authenService.authen(user.getUsername(), user.getPassword());
	}

	@PutMapping("/logout")
	public boolean logout(String username) throws BusinessException {
		return true;
	}
}
