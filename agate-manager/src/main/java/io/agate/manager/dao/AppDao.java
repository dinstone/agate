/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
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
package io.agate.manager.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.agate.manager.entity.AppEntity;

@Component
public class AppDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean appNameExist(String name) {
		String sql = "select count(1) from t_app where name=?";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { name }, Integer.class);
		return count != null && count > 0;
	}

	public void create(AppEntity entity) {
		String sql = "insert into t_app(id,name,gwId,json,createtime,updatetime) values(?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { entity.getId(), entity.getName(), entity.getGwId(), entity.getJson(),
				entity.getCreateTime(), entity.getUpdateTime() });
	}

	public void update(AppEntity entity) {
		String sql = "update t_app set name=?,gwId=?,json=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { entity.getName(), entity.getGwId(), entity.getJson(),
				entity.getUpdateTime(), entity.getId() });
	}

	public List<AppEntity> list() {
		String sql = "select * from t_app";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(AppEntity.class));
	}

	public AppEntity find(Integer id) {
		String sql = "select * from t_app where id=?";
		List<AppEntity> ares = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(AppEntity.class));
		if (!ares.isEmpty()) {
			return ares.get(0);
		}
		return null;
	}

	public void delete(Integer id) {
		String sql = "delete from t_app where id=?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

}
