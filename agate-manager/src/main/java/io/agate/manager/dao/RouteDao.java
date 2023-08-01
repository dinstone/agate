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

import io.agate.manager.entity.RouteEntity;

@Component
public class RouteDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean routeNameExist(String name) {
		String sql = "select count(1) from t_route where name=?";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { name }, Integer.class);
		return count != null && count > 0;
	}

	public void create(RouteEntity entity) {
		String sql = "insert into t_route(appId,name,status,json,createtime,updatetime) values(?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { entity.getAppId(), entity.getName(), entity.getStatus(),
				entity.getJson(), entity.getCreateTime(), entity.getUpdateTime() });
	}

	public void update(RouteEntity entity) {
		String sql = "update t_route set appId=?,name=?,json=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { entity.getAppId(), entity.getName(), entity.getJson(),
				entity.getUpdateTime(), entity.getId() });
	}

	public List<RouteEntity> list() {
		String sql = "select * from t_route";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(RouteEntity.class));
	}

	public List<RouteEntity> list(Integer appId) {
		String sql = "select * from t_route where appId=?";
		return jdbcTemplate.query(sql, new Object[] { appId }, BeanPropertyRowMapper.newInstance(RouteEntity.class));
	}
	
	public List<RouteEntity> listByGatewayId(Integer gwId) {
		String sql = "select r.* from t_route r, t_app a where r.appId=a.id and a.gwId=?";
		return jdbcTemplate.query(sql, new Object[] { gwId }, BeanPropertyRowMapper.newInstance(RouteEntity.class));
	}

	public RouteEntity find(Integer id) {
		String sql = "select * from t_route where id=?";
		List<RouteEntity> ares = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(RouteEntity.class));
		if (!ares.isEmpty()) {
			return ares.get(0);
		}
		return null;
	}

	public void delete(Integer id) {
		String sql = "delete from t_route where id=?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

	public void updateStatus(RouteEntity entity) {
		String sql = "update t_route set status=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { entity.getStatus(), entity.getUpdateTime(), entity.getId() });
	}

	public void deleteByGatewayId(Integer gwId) {
		String sql = "delete from t_route where gwId=?";
		jdbcTemplate.update(sql, new Object[] { gwId });
	}

}
