/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
