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

package com.dinstone.agate.gateway.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.agate.gateway.options.RouteOptions;

public class RoundRobinLoadBalancer implements Loadbalancer {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);

    private AtomicInteger position = new AtomicInteger();

    private RouteOptions routeOptions;

    private ServiceAddressSupplier supplier;

    public RoundRobinLoadBalancer(RouteOptions routeOptions, ServiceAddressSupplier supplier) {
        this.routeOptions = routeOptions;
        this.supplier = supplier;
    }

    @Override
    public String choose() {
        List<ServiceAddress> serviceAddresses = supplier.get();
        if (serviceAddresses.isEmpty()) {
            LOG.warn("No available servers for route: " + routeOptions.getRoute());
            return null;
        }

        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0).getUrl();
        }

        int pos = Math.abs(this.position.getAndIncrement());
        return serviceAddresses.get(pos % serviceAddresses.size()).getUrl();
    }

}
