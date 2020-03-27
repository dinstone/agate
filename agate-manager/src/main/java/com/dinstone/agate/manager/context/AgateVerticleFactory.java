/*
 * Copyright (C) 2019~2020 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.context;

import java.util.concurrent.Callable;

import com.dinstone.agate.manager.verticle.ManageVerticle;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;

public class AgateVerticleFactory implements VerticleFactory {

	private static final String AGATE_PREFIX = "agate";

	private ApplicationContext context;

	public AgateVerticleFactory(ApplicationContext context) {
		this.context = context;
	}

	public static String appendPrefix(Class<?> clazz) {
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
		if (ManageVerticle.class.getName().equals(clazz)) {
			return new ManageVerticle(context);
		}
//        else if (DeployVerticle.class.getName().equals(clazz)) {
//            return new DeployVerticle(context);
//        } else if (ManageVerticle.class.getName().equals(clazz)) {
//            return new ManageVerticle(context);
//        } else if (ServerVerticle.class.getName().equals(clazz)) {
//            return new ServerVerticle(context);
//        }
		throw new IllegalArgumentException("unsupported verticle type: " + clazz);
	}

}
