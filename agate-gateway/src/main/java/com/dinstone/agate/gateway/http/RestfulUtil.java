/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RestfulUtil {

    private static final String CONTENT_TYPE_NAME = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";

    public static void success(RoutingContext ctx) {
        success(ctx, null);
    }

    public static void success(RoutingContext ctx, Object result) {
        JsonObject json = new JsonObject().put("status", "1").put("result", result);
        ctx.response().putHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).end(json.encode());
    }

    public static void failure(RoutingContext ctx, String message) {
        JsonObject json = new JsonObject().put("status", "-1").put("message", message);
        ctx.response().putHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).end(json.encode());
    }

    public static void failure(RoutingContext ctx, Throwable throwable) {
        JsonObject json = new JsonObject().put("status", "-1").put("message", getMessage(throwable));
        ctx.response().putHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).end(json.encode());
    }

    public static void exception(RoutingContext rc, int statusCode, String message) {
        JsonObject json = new JsonObject().put("status", statusCode).put("message", message);
        rc.response().setStatusCode(statusCode).putHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).end(json.encode());
    }

    public static void exception(RoutingContext rc, int statusCode, Throwable failure) {
        JsonObject json = new JsonObject().put("status", statusCode).put("message", getMessage(failure));
        rc.response().setStatusCode(statusCode).putHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON).end(json.encode());
    }

    private static String getMessage(Throwable t) {
        if (t == null) {
            return "";
        }
        String message = t.getMessage();
        if (message == null) {
            return getMessage(t.getCause());
        }
        return message;
    }
}
