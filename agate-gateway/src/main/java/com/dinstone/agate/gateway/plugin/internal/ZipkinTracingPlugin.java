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
package com.dinstone.agate.gateway.plugin.internal;

import com.dinstone.agate.gateway.handler.OperationHandler;
import com.dinstone.agate.gateway.handler.internal.ZipkinTracingHandler;
import com.dinstone.agate.gateway.options.RouteOptions;
import com.dinstone.agate.gateway.plugin.PluginOptions;
import com.dinstone.agate.gateway.plugin.RoutePlugin;

import io.vertx.core.Vertx;

public class ZipkinTracingPlugin extends RoutePlugin {

    public ZipkinTracingPlugin(RouteOptions routeOptions, PluginOptions pluginOptions) {
        super(routeOptions, pluginOptions);
    }

    @Override
    public OperationHandler createHandler(Vertx vertx) {
        return new ZipkinTracingHandler(routeOptions);
    }

}
