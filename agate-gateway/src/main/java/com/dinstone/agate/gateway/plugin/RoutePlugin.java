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
package com.dinstone.agate.gateway.plugin;

import com.dinstone.agate.gateway.handler.OperationHandler;
import com.dinstone.agate.gateway.options.RouteOptions;

import io.vertx.core.Vertx;

public abstract class RoutePlugin {

    protected RouteOptions routeOptions;
    protected PluginOptions pluginOptions;

    public RoutePlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
        this.routeOptions = routeOptions;
        this.pluginOptions = pluginOptions;
    }

    public abstract OperationHandler createHandler(Vertx vertx);

    public void destory() {
    }
}
