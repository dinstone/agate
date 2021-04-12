package com.dinstone.agate.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.dao.ClusterDao;
import com.dinstone.agate.manager.model.ClusterEntity;
import com.dinstone.agate.manager.model.NodeEntity;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

@Component
public class ClusterService {

    private List<NodeEntity> clusterNodes = new CopyOnWriteArrayList<>();

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private ClusterDao clusterDao;

    public List<ClusterEntity> clusterList() {
        return clusterDao.list();
    }

    public List<ClusterEntity> clusterStatus() {
        List<ClusterEntity> ces = clusterDao.list();
        if (ces != null) {
            for (ClusterEntity ce : ces) {
                findNodes(ce);
            }
        }
        return ces;
    }

    private void findNodes(ClusterEntity ce) {
        for (NodeEntity ne : clusterNodes) {
            if (ce.getCode().equals(ne.getClusterCode())) {
                ce.getNodes().add(ne);
            }
        }
    }

    public void refresh() {
        List<NodeEntity> entities = new ArrayList<>();

        ConsulResponse<List<CatalogService>> consulResponse = catalogClient.getService("agate-gateway");
        for (CatalogService e : consulResponse.getResponse()) {
            NodeEntity node = createNode(e.getServiceMeta());
            entities.add(node);
        }

        clusterNodes.clear();
        clusterNodes.addAll(entities);
    }

    private NodeEntity createNode(Map<String, String> serviceMeta) {
        NodeEntity node = new NodeEntity();
        try {
            if (serviceMeta != null) {
                node.setInstanceId(serviceMeta.get("instanceId"));
                node.setClusterCode(serviceMeta.get("clusterCode"));
            }
        } catch (Exception e) {
            // ignore
        }
        return node;
    }

    public void createCluster(ClusterEntity entity) throws BusinessException {
        // app param check

        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        try {
            clusterDao.create(entity);
        } catch (Exception e) {
            throw new BusinessException(40110, "Cluster is exit");
        }
    }

    public void updateCluster(ClusterEntity entity) throws BusinessException {
        // app logic check
        if (entity.getId() == null) {
            throw new BusinessException(40108, "Cluster id is invalid");
        }

        // app param check

        ClusterEntity ue = clusterDao.find(entity.getId());
        if (ue == null) {
            throw new BusinessException(40109, "can't find cluster");
        }

        ue.setCode(entity.getCode());
        ue.setName(entity.getName());
        ue.setUpdateTime(new Date());
        clusterDao.update(ue);
    }

    public void deleteCluster(Integer id) throws BusinessException {
        if (id == null) {
            throw new BusinessException(40108, "Cluster id is invalid");
        }

        clusterDao.delete(id);
    }

    public ClusterEntity getClusterById(Integer id) {
        return clusterDao.find(id);
    }

}
