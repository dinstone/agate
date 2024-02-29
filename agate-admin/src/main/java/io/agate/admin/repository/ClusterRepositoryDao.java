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
package io.agate.admin.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.agate.admin.business.model.ClusterDefinition;
import io.agate.admin.business.port.ClusterRepository;
import io.agate.admin.repository.entity.ClusterEntity;

@Component
public class ClusterRepositoryDao implements ClusterRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean clusterNameExist(String code) {
		String sql = "select count(1) from t_cluster where code=?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
		return count != null && count > 0;
	}

	@Override
	public void create(ClusterDefinition definition) {
		ClusterEntity entity = new ClusterEntity();
		entity.setCode(definition.getCode());
		entity.setName(definition.getName());
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		String sql = "insert into t_cluster(code,name,createtime,updatetime) values(?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { definition.getCode(), definition.getName(), now, now });
	}

	@Override
	public void update(ClusterDefinition definition) {
		String sql = "update t_cluster set name=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { definition.getName(), new Date(), definition.getId() });
	}

	@Override
	public List<ClusterDefinition> list() {
		String sql = "select * from t_cluster";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ClusterDefinition.class));
	}

	@SuppressWarnings("unused")
	private ClusterDefinition convert(ClusterEntity ce) {
		ClusterDefinition cd = new ClusterDefinition();
		cd.setId(ce.getId());
		cd.setCode(ce.getCode());
		cd.setName(ce.getName());
		return cd;
	}

	@Override
	public ClusterDefinition find(Integer id) {
		String sql = "select * from t_cluster where id=?";
		List<ClusterDefinition> apps = jdbcTemplate.query(sql,
				BeanPropertyRowMapper.newInstance(ClusterDefinition.class), id);
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	@Override
	public ClusterDefinition find(String code) {
		String sql = "select * from t_cluster where code=?";
		List<ClusterDefinition> apps = jdbcTemplate.query(sql,
				BeanPropertyRowMapper.newInstance(ClusterDefinition.class), code);
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	@Override
	public void delete(Integer id) {
		String sql = "delete from t_cluster where id=?";
		jdbcTemplate.update(sql, id);
	}

}
