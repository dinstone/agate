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

package io.agate.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import io.agate.admin.bootstrap.ApplicationBootstrap;

@SpringBootApplication
public class ApplicationLauncher implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationLauncher.class);

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = SpringApplication.run(ApplicationLauncher.class, args);
        } catch (Exception e) {
            LOG.error("agate admin application start error", e);
            if (context != null) {
                SpringApplication.exit(context, new ExitCodeGenerator() {

                    @Override
                    public int getExitCode() {
                        return -1;
                    }
                });
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ApplicationBootstrap bootstrap = new ApplicationBootstrap(applicationContext);
        try {
            bootstrap.start();
            // add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread("ShutdownHook") {

                @Override
                public void run() {
                    bootstrap.stop();
                }
            });
        } catch (Exception e) {
            bootstrap.stop();

            throw e;
        }
    }

}
