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
package com.dinstone.agate.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.agate.manager.model.ClusterEntity;
import com.dinstone.agate.manager.service.BusinessException;
import com.dinstone.agate.manager.service.ClusterService;

@Controller
@RequestMapping("/view/cluster")
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    @RequestMapping("/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("cluster/list");
        List<ClusterEntity> clusterList = clusterService.clusterStatus();
        return mav.addObject("clusters", clusterList);
    }

    @RequestMapping("/create")
    public ModelAndView create() {
        ModelAndView mav = new ModelAndView("cluster/edit");
        return mav.addObject("action", "create");
    }

    @RequestMapping("/update")
    public ModelAndView update(Integer id) {
        try {
            ClusterEntity entity = clusterService.getClusterById(id);
            ModelAndView mav = new ModelAndView("cluster/edit").addObject("cluster", entity);
            return mav.addObject("action", "update");
        } catch (Exception e) {
            return new ModelAndView("cluster/list");
        }
    }

    @RequestMapping("/save")
    public ModelAndView save(ClusterEntity entity, String action) {
        try {
            if ("create".equals(action)) {
                clusterService.createCluster(entity);
            } else {
                clusterService.updateCluster(entity);
            }
        } catch (BusinessException e) {
            ModelAndView mav = new ModelAndView("forward:/view/cluster/" + action);
            mav.addObject("error", e.getMessage());
            return mav.addObject("cluster", entity).addObject("action", action);
        }
        return new ModelAndView("forward:/view/cluster/list");
    }

    @RequestMapping("/detail")
    public ModelAndView detail(Integer id) {
        try {
            ClusterEntity entity = clusterService.getClusterById(id);

            return new ModelAndView("cluster/detail").addObject("cluster", entity);
        } catch (Exception e) {
            return new ModelAndView("forward:/view/cluster/list");
        }
    }

    @RequestMapping("/delete")
    public ModelAndView delete(Integer id) {
        try {
            clusterService.deleteCluster(id);
        } catch (BusinessException e) {
        }
        return new ModelAndView("forward:/view/cluster/list");
    }

}
