/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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
package io.agate.gateway.plugin;

import io.agate.gateway.handler.RouteHandler;
import io.agate.gateway.options.RouteOptions;
import io.vertx.core.Vertx;

/**
 * The RouteHandlerPlugin is responsible for creating a RouteHandler.
 */
public abstract class RouteHandlerPlugin {

    public abstract RouteHandler createHandler(Vertx vertx, RouteOptions routeOptions, PluginOptions pluginOptions);

    public boolean failure() {
        return false;
    }

    public void destroy() {
    }
}
