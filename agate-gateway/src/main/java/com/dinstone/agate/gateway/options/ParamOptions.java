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

import io.vertx.core.json.JsonObject;

public class ParamOptions {

    private String feParamName;

    /**
     * Header/Query/Path
     */
    private ParamType feParamType;

    private String beParamName;

    /**
     * Header/Query/Path
     */
    private ParamType beParamType;

    public ParamOptions() {
        super();
    }

    public ParamOptions(JsonObject json) {
        fromJson(json);
    }

    public String getFeParamName() {
        return feParamName;
    }

    public void setFeParamName(String feParamName) {
        this.feParamName = feParamName;
    }

    public ParamType getFeParamType() {
        return feParamType;
    }

    public void setFeParamType(ParamType feParamType) {
        this.feParamType = feParamType;
    }

    public String getBeParamName() {
        return beParamName;
    }

    public void setBeParamName(String beParamName) {
        this.beParamName = beParamName;
    }

    public ParamType getBeParamType() {
        return beParamType;
    }

    public void setBeParamType(ParamType beParamType) {
        this.beParamType = beParamType;
    }

    public void fromJson(JsonObject json) {
        for (java.util.Map.Entry<String, Object> member : json) {
            switch (member.getKey()) {
            case "feParamName":
                if (member.getValue() instanceof String) {
                    this.setFeParamName((String) member.getValue());
                }
                break;
            case "feParamType":
                if (member.getValue() instanceof String) {
                    String v = (String) member.getValue();
                    this.setFeParamType(ParamType.valueOf(v.toUpperCase()));
                }
                break;
            case "beParamName":
                if (member.getValue() instanceof String) {
                    this.setBeParamName((String) member.getValue());
                }
                break;
            case "beParamType":
                if (member.getValue() instanceof String) {
                    String v = (String) member.getValue();
                    this.setBeParamType(ParamType.valueOf(v.toUpperCase()));
                }
                break;
            }
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (feParamName != null) {
            json.put("feParamName", feParamName);
        }
        if (feParamType != null) {
            json.put("feParamType", feParamType.toString());
        }
        if (beParamName != null) {
            json.put("beParamName", beParamName);
        }
        if (beParamType != null) {
            json.put("beParamType", beParamType.toString());
        }

        return json;
    }

}
