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
package io.agate.gateway.http;

import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.QueryStringEncoder;

public class QueryCoder {

    private QueryStringDecoder decoder;

    private QueryStringEncoder encoder;

    public QueryCoder(String url) {
        decoder = new QueryStringDecoder(url);
        Map<String, List<String>> params = decoder.parameters();

        encoder = new QueryStringEncoder(decoder.uri());
        params.forEach((k, vs) -> {
            vs.forEach((v -> encoder.addParam(k, v)));
        });
    }

    public QueryCoder addParam(String k, String v) {
        encoder.addParam(k, v);
        return this;
    }

    public String uri() {
        return encoder.toString();
    }

    public String raw() {
        return decoder.toString();
    }

}
