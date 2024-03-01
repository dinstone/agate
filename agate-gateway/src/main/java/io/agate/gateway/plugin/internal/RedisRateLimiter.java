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
package io.agate.gateway.plugin.internal;

import io.agate.gateway.options.RouteOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class RedisRateLimiter {
    private final Vertx vertx;
    private final JsonObject pluginOptions;

    private static final int MAX_RECONNECT_RETRIES = 16;

    private final AtomicBoolean CONNECTING = new AtomicBoolean();
    private final Redis redisClient;
    private final String limitScript;
    private final int limitPeriod;
    private final int limitCount;
    private final String limitLock;

    public RedisRateLimiter(Vertx vertx, RouteOptions routeOptions, JsonObject pluginOptions) {
        this.vertx = vertx;
        this.pluginOptions = pluginOptions;

        this.limitScript = buildLuaScript();
        this.redisClient = Redis.createClient(vertx, getRedisOptions(pluginOptions));

        this.limitLock = "limiter:" + routeOptions.getRoute();

        JsonObject limitOptions = getLimitOptions(pluginOptions);
        this.limitCount = limitOptions.getInteger("count", 5);
        this.limitPeriod = limitOptions.getInteger("period", 1) * 1000;
    }

    private JsonObject getLimitOptions(JsonObject pluginOptions) {
        return pluginOptions.getJsonObject("limit", new JsonObject());
    }

    private RedisOptions getRedisOptions(JsonObject pluginOptions) {
        return new RedisOptions(pluginOptions.getJsonObject("redis", new JsonObject()));
    }

    private String buildLuaScript() {
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

    public Future<Boolean> acquire() {
        Promise<Boolean> promise = Promise.promise();
        redisClient.connect().onSuccess(conn -> {
            // connected to redis!
            long endTime = System.currentTimeMillis();
            long startTime = endTime - limitPeriod;
            conn.send(Request.cmd(Command.EVAL, limitScript, 1, limitLock, endTime, startTime, limitCount)).onSuccess(r -> {
                promise.complete(r.toBoolean());
                conn.close();
            }).onFailure(promise::fail);
        }).onFailure(promise::fail);
        return promise.future();
    }

    private Future<RedisConnection> createRedisClient() {
        Promise<RedisConnection> promise = Promise.promise();

        if (CONNECTING.compareAndSet(false, true)) {
            // make sure to invalidate old connection if present
            if (redisClient != null) {
                redisClient.close();
            }

            redisClient.connect().onSuccess(conn -> {
                // make sure the client is reconnected on error
                // eg, the underlying TCP connection is closed but the client side doesn't know it yet
                //     the client tries to use the staled connection to talk to server. An exceptions will be raised
                conn.exceptionHandler(e -> {
                    attemptReconnect(0);
                });

                // make sure the client is reconnected on connection close
                // eg, the underlying TCP connection is closed with normal 4-Way-Handshake
                //     this handler will be notified instantly
                conn.endHandler(placeHolder -> {
                    attemptReconnect(0);
                });

                // allow further processing
                promise.complete(conn);
                CONNECTING.set(false);
            }).onFailure(t -> {
                promise.fail(t);
                CONNECTING.set(false);
            });
        } else {
            promise.complete();
        }

        return promise.future();
    }

    /**
     * Attempt to reconnect up to MAX_RECONNECT_RETRIES
     */
    private void attemptReconnect(int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            // we should stop now, as there's nothing we can do.
            CONNECTING.set(false);
        } else {
            // retry with backoff up to 10240 ms
            long backoff = (long) (Math.pow(2, Math.min(retry, 10)) * 10);

            vertx.setTimer(backoff, timer -> {
                createRedisClient()
                        .onFailure(t -> attemptReconnect(retry + 1));
            });
        }
    }

    public int getCount() {
        return limitCount;
    }

    public int getPeriod() {
        return limitPeriod;
    }

    public void destroy() {
        redisClient.close();
    }
}


