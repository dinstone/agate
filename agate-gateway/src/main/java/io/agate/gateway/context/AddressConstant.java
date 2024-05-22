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
package io.agate.gateway.context;

public interface AddressConstant {

    String GATEWAY_START = "gateway:start";
    String GATEWAY_CLOSE = "gateway:close";

    String ROUTE_DEPLOY = "route:deploy";
    String ROUTE_REMOVE = "route:remove";
    String ROUTE_UPDATE = "route:update";

    String APM_METRICS = "apm.metrics";

    String CIRCUIT_BREAKER = "vertx.circuit-breaker";

}
