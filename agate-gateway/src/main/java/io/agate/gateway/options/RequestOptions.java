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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RequestOptions {

    private String prefix;

    private String path;

    private String method;

    private String[] consumes;

    private String[] produces;

    public RequestOptions(JsonObject json) {
        fromJson(json);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getConsumes() {
        return consumes;
    }

    public void setConsumes(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (prefix != null) {
            json.put("prefix", prefix);
        }
        if (path != null) {
            json.put("path", path);
        }
        if (method != null) {
            json.put("method", method);
        }
        if (consumes != null) {
            json.put("consumes", Arrays.asList(consumes));
        }
        if (produces != null) {
            json.put("produces", Arrays.asList(produces));
        }

        return json;
    }

    public void fromJson(JsonObject json) {
        for (java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
            case "prefix":
                if (member.getValue() instanceof String) {
                    this.setPrefix((String) member.getValue());
                }
                break;
            case "path":
                if (member.getValue() instanceof String) {
                    this.setPath((String) member.getValue());
                }
                break;
            case "method":
                if (member.getValue() instanceof String) {
                    this.setMethod((String) member.getValue());
                }
                break;
            case "consumes":
                if (member.getValue() instanceof JsonArray) {
                    List<String> cl = new ArrayList<>();
                    ((JsonArray) member.getValue()).forEach(m -> {
                        if (m instanceof String) {
                            cl.add((String) m);
                        }
                    });
                    this.setConsumes(cl.toArray(new String[0]));
                } else if (member.getValue() instanceof String) {
                    this.setConsumes(toArray((String) member.getValue()));
                }
                break;
            case "produces":
                if (member.getValue() instanceof JsonArray) {
                    List<String> cl = new ArrayList<>();
                    ((JsonArray) member.getValue()).forEach(m -> {
                        if (m instanceof String) {
                            cl.add((String) m);
                        }
                    });
                    this.setProduces(cl.toArray(new String[0]));
                } else if (member.getValue() instanceof String) {
                    this.setProduces(toArray((String) member.getValue()));
                }
                break;
            }
        }
    }

    private String[] toArray(String sv) {
        if (sv == null || sv.isEmpty()) {
            return null;
        }
        return sv.split(",");
    }
}
