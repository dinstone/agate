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
