/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.agate.gateway.verticle.LaunchVerticle;

public class AgateGatewayActivator {

	private static final Logger LOG = LoggerFactory.getLogger(AgateGatewayActivator.class);

	public static void main(String[] args) {
		// launch application activator
		AgateGatewayActivator activator = new AgateGatewayActivator();
		try {
			long s = System.currentTimeMillis();
			activator.start();
			long e = System.currentTimeMillis();
			LOG.info("application startup in {} ms.", (e - s));
		} catch (Exception e) {
			LOG.error("application startup error.", e);
			activator.stop();

			System.exit(-1);
		}
	}

	public void start() throws Exception {
		AgateGatewayLauncher.main(new String[] { "run", LaunchVerticle.class.getName() });
	}

	public void stop() {
		AgateGatewayLauncher.main(new String[] { "stop", LaunchVerticle.class.getName() });
	}

}
