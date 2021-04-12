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
package com.dinstone.agate.gateway.options;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RoutingOptions {

    private int timeout;

    private String method;

    private List<String> urls;

    private List<ParamOptions> params;

    public RoutingOptions(JsonObject json) {
        fromJson(json);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<ParamOptions> getParams() {
        return params;
    }

    public void setParams(List<ParamOptions> params) {
        this.params = params;
    }

    public void fromJson(JsonObject json) {
        for (java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
            case "timeout":
                if (member.getValue() instanceof Number) {
                    this.setTimeout(((Number) member.getValue()).intValue());
                }
                break;
            case "method":
                if (member.getValue() instanceof String) {
                    this.setMethod((String) member.getValue());
                }
                break;
            case "urls":
                if (member.getValue() instanceof JsonArray) {
                    List<String> cl = new ArrayList<>();
                    ((JsonArray) member.getValue()).forEach(m -> {
                        if (m instanceof String) {
                            cl.add((String) m);
                        }
                    });
                    this.setUrls(cl);
                }
                break;
            case "params":
                if (member.getValue() instanceof JsonArray) {
                    List<ParamOptions> cl = new ArrayList<>();
                    ((JsonArray) member.getValue()).forEach(m -> {
                        if (m instanceof JsonObject) {
                            cl.add(new ParamOptions((JsonObject) m));
                        }
                    });
                    this.setParams(cl);
                }
                break;
            }
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.put("timeout", timeout);
        if (method != null) {
            json.put("method", method);
        }
        if (urls != null) {
            json.put("urls", urls);
        }
        if (params != null) {
            json.put("params", params);
        }

        return json;
    }

}
