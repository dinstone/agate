/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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
package com.dinstone.agate.gateway.http;

import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ResponseUtil {

    public static void success(RoutingContext ctx) {
        success(ctx, null);
    }

    public static void success(RoutingContext ctx, Object result) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "1");
        res.put("message", "success");
        if (result != null) {
            res.put("result", result);
        }
        ctx.response().end(Json.encode(res));
    }

    public static void failed(RoutingContext ctx, String message) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "-1");
        res.put("message", message);
        ctx.response().end(Json.encode(res));
    }

    public static void failed(RoutingContext ctx, Throwable throwable) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", "-1");
        res.put("message", throwable == null ? "" : throwable.getMessage());
        ctx.response().end(Json.encode(res));
    }

}
