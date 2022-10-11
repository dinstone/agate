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
package io.agate.manager;

import java.util.List;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;
import com.orbitz.consul.model.health.Node;

public class ConsulClientTest {

    public static void main(String[] args) {
        CatalogClient cc = Consul.builder().build().catalogClient();
        for (Node node : cc.getNodes().getResponse()) {
            System.out.println(node.getNode() + ":" + node.getAddress() + ":" + node.getNodeMeta());
        }

        service(cc);
    }

    private static void service(CatalogClient cc) {
        ConsulResponse<List<CatalogService>> rl = cc.getService("agate-gateway");
        for (CatalogService e : rl.getResponse()) {
            System.out.println(e.getServiceName() + " : " + e.getServiceId() + " : " + e.getServiceMeta());
        }
    }

    private static void kv() {
        KeyValueClient kvc = Consul.builder().build().keyValueClient();

        String key = "agate/apps/default/gw-app";
        String value = "{\n" + "    \"cluster\": \"default\",\n" + "    \"appName\": \"gw-app\",\n"
                + "    \"prefix\": \"/consul\",\n" + "    \"apiName\": \"consul\",\n" + "    \"path\": \"/*\",\n"
                + "    \"rateLimit\": {\n" + "        \"permitsPerSecond\": 5000\n" + "    },\n"
                + "    \"backend\": {\n" + "        \"params\": [\n" + "            {\n"
                + "                \"feParamName\": \"path\",\n" + "                \"feParamType\": \"PATH\",\n"
                + "                \"beParamName\": \"path\",\n" + "                \"beParamType\": \"path\"\n"
                + "            }\n" + "        ],\n" + "        \"urls\": [\n"
                + "            \"http://localhost:8500/ui/dc1/intentions\",\n"
                + "            \"http://localhost:8500/ui/dc1/intentions\"\n" + "        ]\n" + "    }\n" + "}";
        boolean ok = kvc.putValue(key, value);

        System.out.println(ok);
    }

}
