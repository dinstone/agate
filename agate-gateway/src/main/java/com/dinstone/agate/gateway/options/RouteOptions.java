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

package com.dinstone.agate.gateway.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dinstone.agate.gateway.plugin.PluginOptions;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RouteOptions {

    private String cluster;

    private String gateway;

    private String route;

    // ====================
    private RequestOptions request;

    private ResponseOptions response;

    // ====================
    private PluginOptions[] failures;

    private PluginOptions[] befores;

    private PluginOptions[] afters;

    private PluginOptions routing;

    public RouteOptions(JsonObject json) {
        fromJson(json);
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public RequestOptions getRequest() {
        return request;
    }

    public void setRequest(RequestOptions frontend) {
        this.request = frontend;
    }

    public ResponseOptions getResponse() {
        return response;
    }

    public void setResponse(ResponseOptions response) {
        this.response = response;
    }

    public PluginOptions[] getFailures() {
        return failures;
    }

    public void setFailures(PluginOptions[] failures) {
        this.failures = failures;
    }

    public PluginOptions[] getBefores() {
        return befores;
    }

    public void setBefores(PluginOptions[] befores) {
        this.befores = befores;
    }

    public PluginOptions[] getAfters() {
        return afters;
    }

    public void setAfters(PluginOptions[] afters) {
        this.afters = afters;
    }

    public void setRouting(PluginOptions routing) {
        this.routing = routing;
    }

    public PluginOptions getRouting() {
        return routing;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (cluster != null) {
            json.put("cluster", cluster);
        }
        if (gateway != null) {
            json.put("gateway", gateway);
        }
        if (request != null) {
            json.put("request", request.toJson());
        }
        if (routing != null) {
            json.put("routing", routing.toJson());
        }
        if (response != null) {
            json.put("response", response.toJson());
        }
        if (befores != null) {
            json.put("befores", Arrays.asList(befores));
        }
        if (afters != null) {
            json.put("afters", Arrays.asList(afters));
        }
        if (failures != null) {
            json.put("failures", Arrays.asList(failures));
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
                case "route":
                    if (member.getValue() instanceof String) {
                        this.setRoute((String) member.getValue());
                    }
                    break;
                case "request":
                    if (member.getValue() instanceof JsonObject) {
                        this.setRequest(new RequestOptions((JsonObject) member.getValue()));
                    }
                    break;
                case "routing":
                    if (member.getValue() instanceof JsonObject) {
                        this.setRouting(new PluginOptions((JsonObject) member.getValue()));
                    }
                    break;
                case "response":
                    if (member.getValue() instanceof JsonObject) {
                        this.setResponse(new ResponseOptions((JsonObject) member.getValue()));
                    }
                    break;
                case "befores":
                    if (member.getValue() instanceof JsonArray) {
                        List<PluginOptions> pol = new ArrayList<>();
                        ((JsonArray) member.getValue()).forEach(m -> {
                            if (m instanceof JsonObject) {
                                pol.add(new PluginOptions((JsonObject) m));
                            }
                        });
                        this.setBefores(pol.toArray(new PluginOptions[0]));
                    }
                    break;
                case "afters":
                    if (member.getValue() instanceof JsonArray) {
                        List<PluginOptions> pol = new ArrayList<>();
                        ((JsonArray) member.getValue()).forEach(m -> {
                            if (m instanceof JsonObject) {
                                pol.add(new PluginOptions((JsonObject) m));
                            }
                        });
                        this.setAfters(pol.toArray(new PluginOptions[0]));
                    }
                    break;
                case "failures":
                    if (member.getValue() instanceof JsonArray) {
                        List<PluginOptions> pol = new ArrayList<>();
                        ((JsonArray) member.getValue()).forEach(m -> {
                            if (m instanceof JsonObject) {
                                pol.add(new PluginOptions((JsonObject) m));
                            }
                        });
                        this.setFailures(pol.toArray(new PluginOptions[0]));
                    }
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "RouteOptions [cluster=" + cluster + ", gateway=" + gateway + ", route=" + route + "]";
    }

}
