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

import java.util.Objects;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class AppOptions {

    private String cluster;

    private String appName;

    private String remark;

    private String prefix;

    private HttpServerOptions serverOptions;

    private HttpClientOptions clientOptions;

    public AppOptions() {
        super();
    }

    public AppOptions(JsonObject json) {
        fromJson(json);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (appName != null) {
            json.put("appName", appName);
        }
        if (cluster != null) {
            json.put("cluster", cluster);
        }
        if (prefix != null) {
            json.put("prefix", prefix);
        }
        if (remark != null) {
            json.put("remark", remark);
        }
        if (serverOptions != null) {
            json.put("serverOptions", serverOptions.toJson());
        }
        if (clientOptions != null) {
            json.put("clientOptions", clientOptions.toJson());
        }

        return json;
    }

    public void fromJson(JsonObject json) {
        for (java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
            case "appName":
                if (member.getValue() instanceof String) {
                    this.setAppName((String) member.getValue());
                }
                break;
            case "cluster":
                if (member.getValue() instanceof String) {
                    this.setCluster((String) member.getValue());
                }
                break;
            case "prefix":
                if (member.getValue() instanceof String) {
                    this.setPrefix((String) member.getValue());
                }
                break;
            case "remark":
                if (member.getValue() instanceof String) {
                    this.setRemark((String) member.getValue());
                }
                break;
            case "serverOptions":
                if (member.getValue() instanceof JsonObject) {
                    this.setServerOptions(new HttpServerOptions((JsonObject) member.getValue()));
                }
                break;
            case "clientOptions":
                if (member.getValue() instanceof JsonObject) {
                    this.setClientOptions(new HttpClientOptions((JsonObject) member.getValue()));
                }
                break;
            }
        }
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public HttpServerOptions getServerOptions() {
        return serverOptions;
    }

    public void setServerOptions(HttpServerOptions serverOptions) {
        this.serverOptions = serverOptions;
    }

    public HttpClientOptions getClientOptions() {
        return clientOptions;
    }

    public void setClientOptions(HttpClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, cluster);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppOptions other = (AppOptions) obj;
        return Objects.equals(appName, other.appName) && Objects.equals(cluster, other.cluster);
    }

}
