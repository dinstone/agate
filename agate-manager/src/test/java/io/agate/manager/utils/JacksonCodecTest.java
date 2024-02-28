/*
 * Copyright (C) 2020~2024 dinstone<dinstone@163.com>
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
package io.agate.manager.utils;

import java.util.ArrayList;
import java.util.List;

import io.agate.admin.business.model.PluginDefinition;
import io.agate.admin.utils.JacksonCodec;

public class JacksonCodecTest {

    public static void main(String[] args) {
        List<PluginDefinition> l = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            PluginDefinition e = new PluginDefinition();
            e.setOrder(j + 1);
            e.setPlugin("p-" + j);
            e.setType(j % 2);
            l.add(e);
        }
        String s = JacksonCodec.encode(l);

        System.out.println(s);

        List<PluginDefinition> o = JacksonCodec.decodeList(s, PluginDefinition.class);
        System.out.println(o);
    }

}
