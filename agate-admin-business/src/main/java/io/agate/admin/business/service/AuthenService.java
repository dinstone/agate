package io.agate.admin.business.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.agate.admin.business.BusinessException;
import io.agate.admin.business.model.UserDefinition;
import io.agate.admin.business.port.AccountRepository;

@Component
public class AuthenService {

	@Autowired
	private AccountRepository accountRepository;

	public UserDefinition authen(String username, String password) throws BusinessException {
		if (username == null || username.isEmpty()) {
			throw new BusinessException(1001, "username is empty");
		}
		if (password == null || password.isEmpty()) {
			throw new BusinessException(1002, "password is empty");
		}

		UserDefinition user = accountRepository.find(username);
		if (user == null || !user.getPassword().equals(password)) {
			throw new BusinessException(1003, "username or password is error");
		}
		user.setPassword(null);
		return user;
	}

}
