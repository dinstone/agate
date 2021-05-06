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

import com.dinstone.agate.manager.model.ClusterEntity;

@Component
public class ClusterDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean clusterNameExist(String code) {
        String sql = "select count(1) from t_cluster where code=?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[] { code }, Integer.class);
        return count != null && count > 0;
    }

    public void create(ClusterEntity entity) {
        String sql = "insert into t_cluster(code,name,createtime,updatetime) values(?,?,?,?)";
        jdbcTemplate.update(sql,
                new Object[] { entity.getCode(), entity.getName(), entity.getCreateTime(), entity.getUpdateTime() });
    }

    public void update(ClusterEntity entity) {
        String sql = "update t_cluster set code=?,name=?,updatetime=? where id=?";
        jdbcTemplate.update(sql,
                new Object[] { entity.getCode(), entity.getName(), entity.getUpdateTime(), entity.getId() });
    }

    public List<ClusterEntity> list() {
        String sql = "select * from t_cluster";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ClusterEntity.class));
    }

    public ClusterEntity find(Integer id) {
        String sql = "select * from t_cluster where id=?";
        List<ClusterEntity> apps = jdbcTemplate.query(sql, new Object[] { id },
                BeanPropertyRowMapper.newInstance(ClusterEntity.class));
        if (!apps.isEmpty()) {
            return apps.get(0);
        }
        return null;
    }

    public ClusterEntity find(String code) {
        String sql = "select * from t_cluster where code=?";
        List<ClusterEntity> apps = jdbcTemplate.query(sql, new Object[] { code },
                BeanPropertyRowMapper.newInstance(ClusterEntity.class));
        if (!apps.isEmpty()) {
            return apps.get(0);
        }
        return null;
    }

    public void delete(Integer id) {
        String sql = "delete from t_cluster where id=?";
        jdbcTemplate.update(sql, new Object[] { id });
    }

}
