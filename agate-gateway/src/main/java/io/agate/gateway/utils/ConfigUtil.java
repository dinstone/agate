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
package io.agate.gateway.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ConfigUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * load config resource from file path or class path.
     * 
     * @param resourceLocation
     * @return
     */
    public static JsonObject loadConfig(String resourceLocation) {
        InputStream resourceStream = null;
        try {
            resourceStream = new FileInputStream(resourceLocation);
        } catch (FileNotFoundException e) {
            LOG.warn("file not found from file path: {}", resourceLocation);
        }

        if (resourceStream == null) {
            resourceStream = getResourceStream(resourceLocation);
        }

        if (resourceStream == null) {
            LOG.warn("file not found from class path:", resourceLocation);
            throw new RuntimeException("failed to load config : " + resourceLocation);
        }

        try (Scanner scanner = new Scanner(resourceStream, "UTF-8").useDelimiter("\\A")) {
            return new JsonObject(scanner.next());
        } catch (Exception e) {
            LOG.error("failed to load config : " + resourceLocation, e);
            throw new RuntimeException("failed to load config : " + resourceLocation, e);
        } finally {
            if (resourceStream != null) {
                try {
                    resourceStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static InputStream getResourceStream(String resource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ConfigUtil.class.getClassLoader();
        }
        return classLoader.getResourceAsStream(resource);
    }

}
