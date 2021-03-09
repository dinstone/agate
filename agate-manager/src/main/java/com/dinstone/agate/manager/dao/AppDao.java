/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import com.dinstone.agate.manager.model.AppEntity;

@Component
public class AppDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean clusterAppExist(AppEntity entity) {
		if (entity.getId() != null) {
			String sql = "select count(1) from t_app where cluster=? and name=? and id<>?";
			Integer count = jdbcTemplate.queryForObject(sql,
					new Object[] { entity.getCluster(), entity.getName(), entity.getId() }, Integer.class);
			return count != null && count > 0;
		} else {
			String sql = "select count(1) from t_app where cluster=? and name=?";
			Integer count = jdbcTemplate.queryForObject(sql, new Object[] { entity.getCluster(), entity.getName() },
					Integer.class);
			return count != null && count > 0;
		}
	}

	public boolean clusterPortExist(AppEntity entity) {
		if (entity.getId() != null) {
			String sql = "select count(1) from t_app where cluster=? and port=? and id<>?";
			Integer count = jdbcTemplate.queryForObject(sql,
					new Object[] { entity.getCluster(), entity.getPort(), entity.getId() }, Integer.class);
			return count != null && count > 0;
		} else {
			String sql = "select count(1)  from t_app where cluster=? and port=?";
			Integer count = jdbcTemplate.queryForObject(sql, new Object[] { entity.getCluster(), entity.getPort() },
					Integer.class);
			return count != null && count > 0;
		}
	}

	public void create(AppEntity entity) {
		String sql = "insert into t_app(cluster,name,host,port,prefix,remark,serverconfig,clientconfig,createtime,updatetime) values(?,?,?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { entity.getCluster(), entity.getName(), entity.getHost(), entity.getPort(),
						entity.getPrefix(), entity.getRemark(), entity.getServerConfig(), entity.getClientConfig(),
						entity.getCreateTime(), entity.getUpdateTime() });
	}

	public void update(AppEntity entity) {
		String sql = "update t_app set cluster=?,name=?,host=?,port=?,prefix=?,remark=?,serverconfig=?,clientconfig=?,updatetime=? where id=?";
		jdbcTemplate.update(sql,
				new Object[] { entity.getCluster(), entity.getName(), entity.getHost(), entity.getPort(),
						entity.getPrefix(), entity.getRemark(), entity.getServerConfig(), entity.getClientConfig(),
						entity.getUpdateTime(), entity.getId() });
	}

	public List<AppEntity> list() {
		String sql = "select * from t_app";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(AppEntity.class));
	}

	public AppEntity find(Integer id) {
		String sql = "select * from t_app where id=?";
		List<AppEntity> apps = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(AppEntity.class));
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	public void delete(Integer id) {
		String sql = "delete from t_app where id=?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

	public void updateStatus(AppEntity app) {
		String sql = "update t_app set status=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { app.getStatus(), app.getUpdateTime(), app.getId() });
	}

}
