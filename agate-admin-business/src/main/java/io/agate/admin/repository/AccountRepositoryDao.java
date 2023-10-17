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
