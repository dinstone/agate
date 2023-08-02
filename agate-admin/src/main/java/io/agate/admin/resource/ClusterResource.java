/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.admin.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dinstone.vertx.web.annotation.Context;
import com.dinstone.vertx.web.annotation.Get;
import com.dinstone.vertx.web.annotation.Produces;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.agate.admin.model.ClusterEntity;
import io.vertx.ext.web.RoutingContext;

@Component
@WebHandler("/cluster")
public class ClusterResource {

    @Get
    @Produces("application/json")
    public List<ClusterEntity> list(@Context RoutingContext rc) {
        List<ClusterEntity> cel = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            ClusterEntity e = new ClusterEntity();
            e.setId(i);
            e.setName("ce-" + i);
            e.setCode("product");
            e.setCreateTime(new Date());

            cel.add(e);
        }
        return cel;
    }

}
