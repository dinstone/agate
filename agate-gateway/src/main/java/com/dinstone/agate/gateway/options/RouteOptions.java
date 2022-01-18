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

import io.vertx.core.json.JsonObject;

public class RouteOptions {

    private String cluster;

    private String gateway;

    private String route;

    // ====================
    private RequestOptions request;

    private RoutingOptions routing;

    private ResponseOptions response;

    // ====================
    private FiltersOptions filters;

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

    public RoutingOptions getRouting() {
        return routing;
    }

    public void setRouting(RoutingOptions backend) {
        this.routing = backend;
    }

    public ResponseOptions getResponse() {
        return response;
    }

    public void setResponse(ResponseOptions response) {
        this.response = response;
    }

    public FiltersOptions getFilters() {
        return filters;
    }

    public void setFilters(FiltersOptions options) {
        this.filters = options;
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
        if (filters != null) {
            json.put("filters", filters.toJson());
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
                        this.setRouting(new RoutingOptions((JsonObject) member.getValue()));
                    }
                    break;
                case "response":
                    if (member.getValue() instanceof JsonObject) {
                        this.setResponse(new ResponseOptions((JsonObject) member.getValue()));
                    }
                    break;
                case "filters":
                    if (member.getValue() instanceof JsonObject) {
                        this.setFilters(new FiltersOptions((JsonObject) member.getValue()));
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
