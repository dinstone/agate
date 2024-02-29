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
                    conn.send(Request.cmd(Command.EVAL, script, "limit:liziba:view", ts - 3000, 5)).onSuccess(r -> {
                        System.out.println(r.get(0));
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
