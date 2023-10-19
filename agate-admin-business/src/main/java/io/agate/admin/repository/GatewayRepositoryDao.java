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

import io.agate.admin.business.model.GatewayDefinition;
import io.agate.admin.business.port.GatewayRepository;
import io.agate.admin.repository.entity.GatewayEntity;
import io.agate.admin.utils.JacksonCodec;

@Component
public class GatewayRepositoryDao implements GatewayRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean gatewayNameExist(GatewayDefinition gateway) {
		if (gateway.getId() != null) {
			String sql = "select count(1) from t_gateway where ccode=? and name=? and id<>?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gateway.getCcode(), gateway.getName(),
					gateway.getId());
			return count != null && count > 0;
		} else {
			String sql = "select count(1) from t_gateway where ccode=? and name=?";
			Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gateway.getCcode(), gateway.getName());
			return count != null && count > 0;
		}
	}

	@Override
	public boolean hasGatewaysByClusterCode(String cluster) {
		String sql = "select count(1) from t_gateway where ccode=?";
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cluster);
		return count != null && count > 0;
	}

	@Override
	public void create(GatewayDefinition definition) {
		GatewayEntity entity = convert(definition);

		Date now = new Date();
		entity.setCreateTime(now);
		entity.setUpdateTime(now);

		String sql = "insert into t_gateway(ccode,name,json,status,createtime,updatetime) values(?,?,?,?,?,?)";
		jdbcTemplate.update(sql, entity.getCcode(), entity.getName(), entity.getJson(), entity.getStatus(),
				entity.getCreateTime(), entity.getUpdateTime());
	}

	private GatewayEntity convert(GatewayDefinition gd) {
		GatewayEntity ge = new GatewayEntity();
		ge.setCcode(gd.getCcode());
		ge.setCname(gd.getCname());
		ge.setId(gd.getId());
		ge.setName(gd.getName());
		ge.setStatus(gd.getStatus());
		ge.setJson(JacksonCodec.encode(gd));
		return ge;
	}

	@Override
	public void update(GatewayDefinition definition) {
		GatewayEntity entity = convert(definition);
		Date now = new Date();
		entity.setUpdateTime(now);

		String sql = "update t_gateway set ccode=?,name=?,json=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, entity.getCcode(), entity.getName(), entity.getJson(), entity.getUpdateTime(),
				entity.getId());
	}

	@Override
	public List<GatewayDefinition> list() {
		String sql = "select * from t_gateway";
		List<GatewayEntity> ges = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class));
		if (!ges.isEmpty()) {
			return ges.stream().map(ge -> convert(ge)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private GatewayDefinition convert(GatewayEntity ge) {
		GatewayDefinition gd = JacksonCodec.decode(ge.getJson(), GatewayDefinition.class);
		gd.setId(ge.getId());
		gd.setCcode(ge.getCcode());
		gd.setCname(ge.getCname());
		gd.setName(ge.getName());
		gd.setStatus(ge.getStatus());
		return gd;
	}

	@Override
	public GatewayDefinition find(Integer id) {
		String sql = "select g.*,c.name cname from t_gateway g, t_cluster c where g.ccode=c.code and g.id=?";
		List<GatewayEntity> apps = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class), id);
		if (!apps.isEmpty()) {
			return convert(apps.get(0));
		}
		return null;
	}

	@Override
	public GatewayDefinition find(String cluster, String gateway) {
		String sql = "select * from t_gateway where ccode=? and name=?";
		List<GatewayEntity> apps = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class),
				cluster, gateway);
		if (!apps.isEmpty()) {
			return convert(apps.get(0));
		}
		return null;
	}

	@Override
	public void delete(Integer id) {
		String sql = "delete from t_gateway where id=?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public void updateStatus(GatewayDefinition definition) {
		String sql = "update t_gateway set status=?,updatetime=? where id=?";
		jdbcTemplate.update(sql, definition.getStatus(), new Date(), definition.getId());
	}

	@Override
	public List<GatewayDefinition> find(String ccode, int start, int size) {
		List<GatewayEntity> ges = null;
		if (ccode != null) {
			String sql = "select g.*,c.name cname from t_gateway g, t_cluster c where g.ccode=c.code and g.ccode=? order by id limit ?,?";
			ges = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class), ccode, start, size);
		} else {
			String sql = "select g.*,c.name cname from t_gateway g, t_cluster c where g.ccode=c.code order by id limit ?,?";
			ges = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(GatewayEntity.class), start, size);
		}

		if (!ges.isEmpty()) {
			return ges.stream().map(ge -> convert(ge)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	@Override
	public int total(String ccode) {
		if (ccode != null) {
			String sql = "select count(1) from t_gateway where ccode=?";
			return jdbcTemplate.queryForObject(sql, Integer.class, ccode);
		} else {
			String sql = "select count(1) from t_gateway";
			return jdbcTemplate.queryForObject(sql, Integer.class);
		}
	}

}
