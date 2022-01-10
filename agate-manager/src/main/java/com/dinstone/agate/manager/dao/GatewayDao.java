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
package com.dinstone.agate.manager.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.model.GatewayEntity;

@Component
public class GatewayDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean gatewayNameExist(GatewayEntity entity) {
		if (entity.getId() != null) {
			String sql = "select count(1) from t_gateway where code=? and name=? and id<>?";
			Integer count = jdbcTemplate.queryForObject(sql,
					new Object[] { entity.getCode(), entity.getName(), entity.getId() }, Integer.class);
			return count != null && count > 0;
		} else {
			String sql = "select count(1) from t_gateway where code=? and name=?";
			Integer count = jdbcTemplate.queryForObject(sql, new Object[] { entity.getCode(), entity.getName() },
					Integer.class);
			return count != null && count > 0;
		}
	}

	public boolean gatewayPortExist(GatewayEntity entity) {
		if (entity.getId() != null) {
			String sql = "select count(1) from t_gateway where code=? and port=? and id<>?";
			Integer count = jdbcTemplate.queryForObject(sql,
					new Object[] { entity.getCode(), entity.getPort(), entity.getId() }, Integer.class);
			return count != null && count > 0;
		} else {
			String sql = "select count(1)  from t_gateway where code=? and port=?";
			Integer count = jdbcTemplate.queryForObject(sql, new Object[] { entity.getCode(), entity.getPort() },
					Integer.class);
			return count != null && count > 0;
		}
	}

	public boolean gatewayCodeExist(String code) {
		String sql = "select count(1) from t_gateway where code=?";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { code }, Integer.class);
		return count != null && count > 0;
	}

	public void create(GatewayEntity entity) {
		String sql = "insert into t_gateway(code,name,host,port,remark,serverconfig,clientconfig,createtime,updatetime) values(?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { entity.getCode(), entity.getName(), entity.getHost(), entity.getPort(),
						entity.getRemark(), entity.getServerConfig(), entity.getClientConfig(), entity.getCreateTime(),
						entity.getUpdateTime() });
	}

	public void update(GatewayEntity entity) {
		String sql = "update t_gateway set code=?,name=?,host=?,port=?,remark=?,serverconfig=?,clientconfig=?,updatetime=? where id=?";
		jdbcTemplate.update(sql,
				new Object[] { entity.getCode(), entity.getName(), entity.getHost(), entity.getPort(),
						entity.getRemark(), entity.getServerConfig(), entity.getClientConfig(), entity.getUpdateTime(),
						entity.getId() });
	}

	public List<GatewayEntity> list() {
		String sql = "select * from t_gateway";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class));
	}

	public GatewayEntity find(Integer id) {
		String sql = "select * from t_gateway where id=?";
		List<GatewayEntity> apps = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(GatewayEntity.class));
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	public GatewayEntity find(String cluster, String gateway) {
		String sql = "select * from t_gateway where code=? and name=?";
		List<GatewayEntity> apps = jdbcTemplate.query(sql, new Object[] { cluster, gateway },
				BeanPropertyRowMapper.newInstance(GatewayEntity.class));
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	public void delete(Integer id) {
		String sql = "delete from t_gateway where id=?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

	public void updateStatus(GatewayEntity entity) {
		String sql = "update t_gateway set status=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { entity.getStatus(), entity.getUpdateTime(), entity.getId() });
	}

	public List<GatewayEntity> all() {
		String sql = "select * from t_gateway";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class));
	}

}
