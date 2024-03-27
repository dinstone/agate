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
package io.agate.admin.business.param;

import io.agate.admin.business.model.AppDefinition;
import io.agate.admin.business.model.GatewayDefinition;

public class AppDetail {

    private AppDefinition app;

    private GatewayDefinition gateway;

    public AppDetail() {
    }

    public AppDetail(AppDefinition app, GatewayDefinition gateway) {
        this.app = app;
        this.gateway = gateway;
    }

    public AppDefinition getApp() {
        return app;
    }

    public void setApp(AppDefinition app) {
        this.app = app;
    }

    public GatewayDefinition getGateway() {
        return gateway;
    }

    public void setGateway(GatewayDefinition gateway) {
        this.gateway = gateway;
    }
}
