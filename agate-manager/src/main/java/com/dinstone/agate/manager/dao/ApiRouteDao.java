/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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

import com.dinstone.agate.manager.model.ApiRouteEntity;

@Component
public class ApiRouteDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean apiNameExist(String name) {
        String sql = "select count(1) from t_api where name=?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[] { name }, Integer.class);
        return count != null && count > 0;
    }

    public void create(ApiRouteEntity entity) {
        String sql = "insert into t_api(gwId,name,remark,request,response,routing,handlers,status,createtime,updatetime) values(?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                new Object[] { entity.getGwId(), entity.getName(), entity.getRemark(), entity.getRequest(),
                        entity.getResponse(), entity.getRouting(), entity.getHandlers(), entity.getStatus(),
                        entity.getCreateTime(), entity.getUpdateTime() });
    }

    public void update(ApiRouteEntity entity) {
        String sql = "update t_api set remark=?,request=?,response=?,routing=?,handlers=?,updatetime=? where arId=?";
        jdbcTemplate.update(sql, new Object[] { entity.getRemark(), entity.getRequest(), entity.getResponse(),
                entity.getRouting(), entity.getHandlers(), entity.getUpdateTime(), entity.getArId() });
    }

    public List<ApiRouteEntity> list() {
        String sql = "select * from t_api";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ApiRouteEntity.class));
    }

    public List<ApiRouteEntity> list(Integer gwId) {
        String sql = "select * from t_api where gwId=?";
        return jdbcTemplate.query(sql, new Object[] { gwId }, BeanPropertyRowMapper.newInstance(ApiRouteEntity.class));
    }

    public ApiRouteEntity find(Integer arId) {
        String sql = "select * from t_api where arId=?";
        List<ApiRouteEntity> ares = jdbcTemplate.query(sql, new Object[] { arId },
                BeanPropertyRowMapper.newInstance(ApiRouteEntity.class));
        if (!ares.isEmpty()) {
            return ares.get(0);
        }
        return null;
    }

    public void delete(Integer arId) {
        String sql = "delete from t_api where arId=?";
        jdbcTemplate.update(sql, new Object[] { arId });
    }

    public void updateStatus(ApiRouteEntity entity) {
        String sql = "update t_api set status=?,updatetime=? where arId=?";
        jdbcTemplate.update(sql, new Object[] { entity.getStatus(), entity.getUpdateTime(), entity.getArId() });
    }

    public void deleteByGatewayId(Integer gwId) {
        String sql = "delete from t_api where gwId=?";
        jdbcTemplate.update(sql, new Object[] { gwId });
    }

}
