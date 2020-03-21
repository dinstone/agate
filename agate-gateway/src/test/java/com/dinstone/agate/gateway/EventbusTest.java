/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
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
package com.dinstone.agate.gateway;

import com.dinstone.agate.gateway.options.AppOptions;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class EventbusTest {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.eventBus().registerDefaultCodec(AppOptions.class, new MessageCodec<AppOptions, AppOptions>() {

            @Override
            public void encodeToWire(Buffer buffer, AppOptions s) {
                // TODO Auto-generated method stub
            }

            @Override
            public AppOptions decodeFromWire(int pos, Buffer buffer) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public AppOptions transform(AppOptions s) {
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
        vertx.eventBus().send("test", new AppOptions());

        System.out.println("ok");
    }

}
