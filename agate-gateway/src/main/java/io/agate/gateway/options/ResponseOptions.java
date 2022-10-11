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
package io.agate.gateway.options;

import io.vertx.core.json.JsonObject;

public class ResponseOptions {

    public ResponseOptions(JsonObject value) {
        fromJson(value);
    }

    public JsonObject toJson() {
        // TODO Auto-generated method stub
        return null;
    }

    public void fromJson(JsonObject value) {
        // TODO Auto-generated method stub

    }

}
