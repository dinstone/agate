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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.agate.admin.business.model.AppDefinition;
import io.agate.admin.business.port.AppRepository;
import io.agate.admin.repository.entity.AppEntity;
import io.agate.admin.utils.JacksonCodec;

@Component
public class AppRepositoryDao implements AppRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean appNameExist(String name) {
		String sql = "select count(1) from t_app where name=?";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { name }, Integer.class);
		return count != null && count > 0;
	}

	@Override
	public void create(AppDefinition defination) {
		AppEntity entity = new AppEntity();
		entity.setGwId(defination.getGwId());
		entity.setName(defination.getName());
		Date createTime = new Date();
		entity.setCreateTime(createTime);
		entity.setUpdateTime(createTime);
		entity.setJson(JacksonCodec.encode(defination));

		String sql = "insert into t_app(id,name,gwId,json,createtime,updatetime) values(?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { entity.getId(), entity.getName(), entity.getGwId(), entity.getJson(),
				entity.getCreateTime(), entity.getUpdateTime() });
	}

	@Override
	public void update(AppDefinition defination) {
		AppEntity entity = new AppEntity();
		entity.setId(defination.getId());
		entity.setGwId(defination.getGwId());
		entity.setName(defination.getName());
		Date updateTime = new Date();
		entity.setUpdateTime(updateTime);
		entity.setJson(JacksonCodec.encode(defination));

		String sql = "update t_app set name=?,gwId=?,json=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, new Object[] { entity.getName(), entity.getGwId(), entity.getJson(),
				entity.getUpdateTime(), entity.getId() });
	}

	@Override
	public List<AppDefinition> list() {
		String sql = "select * from t_app";
		List<AppEntity> es = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(AppEntity.class));
		if (es == null || es.isEmpty()) {
			return Collections.emptyList();
		} else {
			return es.stream().map(ae -> convert(ae)).collect(Collectors.toList());
		}
	}

	@Override
	public AppDefinition find(Integer id) {
		String sql = "select * from t_app where id=?";
		List<AppEntity> ares = jdbcTemplate.query(sql, new Object[] { id },
				BeanPropertyRowMapper.newInstance(AppEntity.class));
		if (!ares.isEmpty()) {
			return convert(ares.get(0));
		}
		return null;
	}

	private AppDefinition convert(AppEntity appEntity) {
		AppDefinition app = JacksonCodec.decode(appEntity.getJson(), AppDefinition.class);
		app.setId(appEntity.getId());
		return app;
	}

	@Override
	public void delete(Integer id) {
		String sql = "delete from t_app where id=?";
		jdbcTemplate.update(sql, new Object[] { id });
	}

}
