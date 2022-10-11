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

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class GatewayOptions {

    private String envCode;

    private String cluster;

    private String gateway;

    private String remark;

    private HttpServerOptions serverOptions;

    private HttpClientOptions clientOptions;

    public GatewayOptions() {
        super();
    }

    public GatewayOptions(JsonObject json) {
        fromJson(json);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (gateway != null) {
            json.put("gateway", gateway);
        }
        if (cluster != null) {
            json.put("cluster", cluster);
        }
        if (envCode != null) {
            json.put("envCode", envCode);
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
            case "gateway":
                if (member.getValue() instanceof String) {
                    this.setGateway((String) member.getValue());
                }
                break;
            case "cluster":
                if (member.getValue() instanceof String) {
                    this.setCluster((String) member.getValue());
                }
                break;
            case "envCode":
                if (member.getValue() instanceof String) {
                    this.setEnvCode((String) member.getValue());
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

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cluster == null) ? 0 : cluster.hashCode());
        result = prime * result + ((envCode == null) ? 0 : envCode.hashCode());
        result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GatewayOptions other = (GatewayOptions) obj;
        if (cluster == null) {
            if (other.cluster != null)
                return false;
        } else if (!cluster.equals(other.cluster))
            return false;
        if (envCode == null) {
            if (other.envCode != null)
                return false;
        } else if (!envCode.equals(other.envCode))
            return false;
        if (gateway == null) {
            if (other.gateway != null)
                return false;
        } else if (!gateway.equals(other.gateway))
            return false;
        return true;
    }

}
