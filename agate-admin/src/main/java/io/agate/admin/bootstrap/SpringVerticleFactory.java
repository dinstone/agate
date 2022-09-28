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
package io.agate.admin.bootstrap;

import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

public class SpringVerticleFactory implements VerticleFactory {

    private static final String PREFIX = "spring";

    private ApplicationContext applicationContext;

    public SpringVerticleFactory(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    public Verticle createVerticle(String verticleName) throws Exception {
        // Our convention in this example is to give the class name as verticle name
        String clazz = VerticleFactory.removePrefix(verticleName);
        return (Verticle) applicationContext.getBean(Class.forName(clazz));
    }

    public static String verticleName(Class<?> verticleClass) {
        return PREFIX + ":" + verticleClass.getName();
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

}
