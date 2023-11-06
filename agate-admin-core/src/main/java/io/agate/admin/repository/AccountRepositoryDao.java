/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.admin.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.agate.admin.business.model.UserDefinition;
import io.agate.admin.business.port.AccountRepository;

@Component
public class AccountRepositoryDao implements AccountRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDefinition find(String username) {

		String sql = "select username,password,enabled from t_user where enabled=1 and username = ?";
		List<UserDefinition> ares = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(UserDefinition.class),
				username);
		if (!ares.isEmpty()) {
			return ares.get(0);
		}
		return null;
	}

}
