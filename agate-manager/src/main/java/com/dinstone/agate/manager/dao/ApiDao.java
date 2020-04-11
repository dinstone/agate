package com.dinstone.agate.manager.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.model.ApiEntity;

@Component
public class ApiDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public boolean apiNameExist(String name) {
		String sql = "select count(1) from t_api where name=?";
		Integer count = jdbcTemplate.queryForObject(sql, new Object[] { name }, Integer.class);
		return count != null && count > 0;
	}

	public void create(ApiEntity entity) {
		String sql = "insert into t_api(appid,name,remark,frontend,backend,status,createtime,updatetime) values(?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql,
				new Object[] { entity.getAppId(), entity.getName(), entity.getRemark(), entity.getFrontend(),
						entity.getBackend(), entity.getStatus(), entity.getCreateTime(), entity.getUpdateTime() });
	}

	public void update(ApiEntity entity) {
		String sql = "update t_api set remark=?,frontend=?,backend=?,updatetime=? where apiid=?";
		jdbcTemplate.update(sql, new Object[] { entity.getRemark(), entity.getFrontend(), entity.getBackend(),
				entity.getUpdateTime(), entity.getApiId() });
	}

	public List<ApiEntity> list(Integer appId) {
		String sql = "select * from t_api where appId=?";
		return jdbcTemplate.query(sql, new Object[] { appId }, BeanPropertyRowMapper.newInstance(ApiEntity.class));
	}

	public ApiEntity find(Integer apiId) {
		String sql = "select * from t_api where apiId=?";
		List<ApiEntity> apps = jdbcTemplate.query(sql, new Object[] { apiId },
				BeanPropertyRowMapper.newInstance(ApiEntity.class));
		if (!apps.isEmpty()) {
			return apps.get(0);
		}
		return null;
	}

	public void delete(Integer apiId) {
		String sql = "delete from t_api where apiId=?";
		jdbcTemplate.update(sql, new Object[] { apiId });
	}

	public void updateStatus(ApiEntity app) {
		String sql = "update t_api set status=?,updatetime=? where apiId=?";
		jdbcTemplate.update(sql, new Object[] { app.getStatus(), app.getUpdateTime(), app.getApiId() });
	}

	public void deleteByAppId(Integer appId) {
		String sql = "delete from t_api where appId=?";
		jdbcTemplate.update(sql, new Object[] { appId });
	}

}
