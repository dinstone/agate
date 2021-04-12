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
import com.dinstone.agate.manager.utils.NamedThreadFactory;

@Component
public class ManagerContext implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerContext.class);

    @Autowired
    private ClusterService clusterManager;

    private ScheduledExecutorService executor;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("gateway-discovery-"));
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    clusterManager.refresh();
                } catch (Exception e) {
                    LOG.warn("agate gateway discovery error: {}", e.getMessage());
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

}
