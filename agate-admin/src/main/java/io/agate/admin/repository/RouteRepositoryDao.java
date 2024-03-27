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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.agate.admin.business.model.RouteDefinition;
import io.agate.admin.business.port.RouteRepository;
import io.agate.admin.repository.entity.RouteEntity;
import io.agate.admin.utils.JsonUtil;

@Component
public class RouteRepositoryDao implements RouteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean routeNameExist(String name) {
        String sql = "select count(1) from t_route where name=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    @Override
    public void create(RouteDefinition definition) {
        RouteEntity entity = convert(definition);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        String sql = "insert into t_route(appId,name,status,json,createtime,updatetime) values(?,?,?,?,?,?)";
        jdbcTemplate.update(sql, new Object[]{entity.getAppId(), entity.getName(), entity.getStatus(),
                entity.getJson(), entity.getCreateTime(), entity.getUpdateTime()});
    }

    private RouteDefinition convert(RouteEntity re) {
        RouteDefinition rd = JsonUtil.decode(re.getJson(), RouteDefinition.class);
        rd.setId(re.getId());
        rd.setName(re.getName());
        rd.setAppId(re.getAppId());
        rd.setStatus(re.getStatus());
        return rd;
    }

    private RouteEntity convert(RouteDefinition definition) {
        RouteEntity entity = new RouteEntity();
        entity.setId(definition.getId());
        entity.setName(definition.getName());
        entity.setAppId(definition.getAppId());
        entity.setStatus(definition.getStatus());

        entity.setJson(JsonUtil.encode(definition));
        return entity;
    }

    @Override
    public void update(RouteDefinition definition) {
        RouteEntity entity = convert(definition);
        entity.setUpdateTime(new Date());

        String sql = "update t_route set appId=?,name=?,json=?,updatetime=? where id=?";
        jdbcTemplate.update(sql, new Object[]{entity.getAppId(), entity.getName(), entity.getJson(),
                entity.getUpdateTime(), entity.getId()});
    }

    @Override
    public List<RouteDefinition> list() {
        String sql = "select * from t_route";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(RouteDefinition.class));
    }

    @Override
    public List<RouteDefinition> list(Integer appId) {
        String sql = "select * from t_route where appId=?";
        List<RouteEntity> aes = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(RouteEntity.class), appId);
        return aes.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public List<RouteDefinition> listByGatewayId(Integer gwId) {
        String sql = "select r.* from t_route r, t_app a where r.appId=a.id and a.gwId=?";
        List<RouteEntity> aes = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(RouteEntity.class), gwId);
        return aes.stream().map(this::convert).collect(Collectors.toList());
    }

    @Override
    public RouteDefinition find(Integer id) {
        String sql = "select * from t_route where id=?";
        List<RouteEntity> ares = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(RouteEntity.class), id);
        if (!ares.isEmpty()) {
            return convert(ares.get(0));
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        String sql = "delete from t_route where id=?";
        jdbcTemplate.update(sql, new Object[]{id});
    }

    @Override
    public void updateStatus(RouteDefinition entity) {
        String sql = "update t_route set status=?,updatetime=? where id=?";
        jdbcTemplate.update(sql, new Object[]{entity.getStatus(), new Date(), entity.getId()});
    }

    @Override
    public void deleteByGatewayId(Integer gwId) {
        String sql = "delete from t_route where gwId=?";
        jdbcTemplate.update(sql, new Object[]{gwId});
    }

    @Override
    public List<RouteDefinition> find(Integer appId, int start, int size) {
        String sql = "select * from t_route where appId=? order by id limit ?,?";
        List<RouteEntity> res = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(RouteEntity.class),
                new Object[]{appId, start, size});
        if (!res.isEmpty()) {
            return res.stream().map(this::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public int total(Integer appId) {
        String sql = "select count(1) from t_route where appId=?";
        Integer t = jdbcTemplate.queryForObject(sql, Integer.class, appId);
        return t == null ? 0 : t;
    }

}
