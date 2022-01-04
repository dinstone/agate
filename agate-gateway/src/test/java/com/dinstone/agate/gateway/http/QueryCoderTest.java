/*
 * Copyright (C) 2019~2021 dinstone<dinstone@163.com>
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
package com.dinstone.agate.gateway.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QueryCoderTest {

    @Test
    void testAddParam() {
        String u = "http://localhost:5256/static/:path?pa=12&pa=34&pb=afasdf";
        QueryCoder qc = new QueryCoder(u);
        qc.addParam("pa", "56");

        String uri = qc.uri();
        assertNotNull(uri);
        assertTrue(uri.startsWith("http://localhost:5256"));
        assertTrue(uri.contains("pa=56"));
        assertTrue(uri.contains("pa=12"));
    }

    @Test
    void testUri() {
        String u = "http://localhost:5256/static/:path";
        QueryCoder qc = new QueryCoder(u);

        assertEquals(qc.uri(), qc.raw());
    }

    @Test
    void testRaw() {
        String u = "http://localhost:5256/static/:path?pa=12&pa=34&pb=afasdf";
        QueryCoder qc = new QueryCoder(u);

        assertNotEquals(qc.uri(), qc.raw());
        assertEquals(u, qc.raw());
    }

}
