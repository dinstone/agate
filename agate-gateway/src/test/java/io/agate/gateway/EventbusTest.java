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
package io.agate.gateway;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.options.GatewayOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

public class EventbusTest {

    private static final Logger LOG = LoggerFactory.getLogger(EventbusTest.class);

    public static void main(String[] args) {

        JsonObject json = getJsonFromFile("src/main/resources/config.json");

        Vertx vertx = Vertx.vertx();

        vertx.eventBus().registerDefaultCodec(GatewayOptions.class, new MessageCodec<GatewayOptions, GatewayOptions>() {

            @Override
            public void encodeToWire(Buffer buffer, GatewayOptions s) {
                // TODO Auto-generated method stub
            }

            @Override
            public GatewayOptions decodeFromWire(int pos, Buffer buffer) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public GatewayOptions transform(GatewayOptions s) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String name() {
                return "app";
            }

            @Override
            public byte systemCodecID() {
                return -1;
            }

        });

        vertx.eventBus().consumer("test").handler(m -> {
            System.out.println(m.body());
        });

        vertx.eventBus().send("test", new GatewayOptions());

        System.out.println("ok");

        vertx.close();

        CompletableFuture<String> cf0 = new CompletableFuture<String>();
        cf0.completeExceptionally(new RuntimeException("Oops"));

        CompletableFuture<String> cf1 = cf0.handle((msg, ex) -> {
            if (ex != null) {
                return "Recovered from \"" + ex.getMessage() + "\"";
            } else {
                return msg;
            }
        });

        try {
            System.out.println("cf1 : " + cf1.get());
            System.out.println(cf0.get());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static JsonObject getJsonFromFile(String jsonFile) {
        if (jsonFile != null) {
            try (Scanner scanner = new Scanner(new File(jsonFile), "UTF-8").useDelimiter("\\A")) {
                String sconf = scanner.next();
                try {
                    return new JsonObject(sconf);
                } catch (DecodeException e) {
                    LOG.error("Configuration file " + sconf + " does not contain a valid JSON object");
                }
            } catch (FileNotFoundException e) {
                LOG.error("unkown know file " + jsonFile, e);
            }
        }

        return null;
    }

}
