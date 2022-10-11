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

package io.agate.gateway.context;

import java.util.concurrent.Callable;

import io.agate.gateway.verticle.DeployVerticle;
import io.agate.gateway.verticle.GatewayVerticle;
import io.agate.gateway.verticle.LaunchVerticle;
import io.agate.gateway.verticle.ManageVerticle;
import io.agate.gateway.verticle.SystemVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

public class AgateVerticleFactory implements VerticleFactory {

    private static final String AGATE_PREFIX = "agate";

    private ApplicationContext context;

    public AgateVerticleFactory(ApplicationContext context) {
        this.context = context;
    }

    public static String verticleName(Class<?> clazz) {
        return AGATE_PREFIX + ":" + clazz.getName();
    }

    @Override
    public String prefix() {
        return AGATE_PREFIX;
    }

    @Override
    public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
        promise.complete(new Callable<Verticle>() {

            @Override
            public Verticle call() throws Exception {
                return createVerticle(verticleName);
            }
        });

    }

    private Verticle createVerticle(String verticleName) {
        String clazz = VerticleFactory.removePrefix(verticleName);
        if (SystemVerticle.class.getName().equals(clazz)) {
            return new SystemVerticle(context);
        } else if (DeployVerticle.class.getName().equals(clazz)) {
            return new DeployVerticle(context);
        } else if (ManageVerticle.class.getName().equals(clazz)) {
            return new ManageVerticle(context);
        } else if (GatewayVerticle.class.getName().equals(clazz)) {
            return new GatewayVerticle(context);
        } else if (LaunchVerticle.class.getName().equals(clazz)) {
            return new LaunchVerticle(context);
        }
        throw new IllegalArgumentException("unsupported verticle type: " + clazz);
    }

}
