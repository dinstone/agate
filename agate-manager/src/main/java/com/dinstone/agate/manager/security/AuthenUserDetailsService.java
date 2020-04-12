package com.dinstone.agate.manager.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenUserDetailsService implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenUserDetailsService.class);

	private static final String ACCOUNT_QUERY = "select username,password,enabled from t_user where username = ?";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetails> users = loadUsersByUsername(username);
		if (users.size() == 0) {
			LOG.debug("Query returned no results for user '{}'", username);

			throw new UsernameNotFoundException("Username '" + username + "' not found");
		}
		return users.get(0);
	}

	protected List<UserDetails> loadUsersByUsername(String username) {
		return jdbcTemplate.query(ACCOUNT_QUERY, new String[] { username }, (rs, rowNum) -> {
			String un = rs.getString(1);
			String pw = rs.getString(2);
			boolean enabled = rs.getBoolean(3);
			return new User(un, pw, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
		});
	}
}