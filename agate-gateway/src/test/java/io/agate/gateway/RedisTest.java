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
package io.agate.gateway;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;

import java.io.IOException;

public class RedisTest {
    public static void main(String[] args) throws IOException {
        String script = buildLuaScript();
        Vertx vertx = Vertx.vertx();
        Redis.createClient(
                        vertx,
                        "redis://localhost:6379")
                .connect()
                .onSuccess(conn -> {
                    // use the connection
                    long ts = System.currentTimeMillis();
                    conn.send(Request.cmd(Command.EVAL, script, 1, "limit:liziba:view", ts, ts - 3000, 5)).onSuccess(r -> {
                        System.out.println(r);
                    }).onFailure(e -> e.printStackTrace());
                });

        System.in.read();

        vertx.close();
    }

    private static String buildLuaScript() {
        return "redis.call('ZREMRANGEBYSCORE',  KEYS[1],  0,  tonumber(ARGV[2])) \n" +
                "local  c  =  redis.call('ZCARD',  KEYS[1]) \n" +
                "if c < tonumber(ARGV[3]) \n" +
                "then \n" +
                "  redis.call('ZADD',  KEYS[1],  tonumber(ARGV[1]),  ARGV[1]) \n" +
                "  return c+1 \n" +
                "else \n" +
                "  return 0 \n" +
                "end \n";
    }
}
