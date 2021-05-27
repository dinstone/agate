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
package com.dinstone.agate.manager.context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.dinstone.agate.manager.service.ClusterService;
import com.dinstone.agate.manager.service.ManageService;
import com.dinstone.agate.manager.utils.NamedThreadFactory;

@Component
public class ManagerContext implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerContext.class);

    @Autowired
    private ClusterService clusterManager;

    @Autowired
    private ManageService manageService;

    private ScheduledExecutorService executor;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("gateway-discovery-"));
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    clusterManager.clusterRefresh();
                } catch (Exception e) {
                    LOG.warn("agate gateway cluster discovery error: {}", e.getMessage());
                }
            }
        }, 1, 3, TimeUnit.SECONDS);

        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    manageService.gatewayRefresh();
                } catch (Exception e) {
                    LOG.warn("agate gateway instance refresh error: {}", e.getMessage());
                }
            }
        }, 3, 5, TimeUnit.SECONDS);
    }

}
