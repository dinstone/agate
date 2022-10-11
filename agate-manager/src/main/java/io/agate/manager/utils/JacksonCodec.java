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

package io.agate.manager.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonCodec {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // JSON configuration not to serialize null field
        objectMapper.setSerializationInclusion(Include.NON_NULL);

        // JSON configuration not to throw exception on empty bean class
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // JSON configuration for compatibility
        objectMapper.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    }

    public static String encode(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode as JSON: " + e.getMessage(), e);
        }
    }

    public static <T> T decode(String str, Class<T> clazz) {
        if (str == null) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> decodeList(String str, Class<T> clazz) {
        if (str == null) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(str, getCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode: " + e.getMessage(), e);
        }
    }

    public static <K, V> Map<K, V> decodeMap(String str, Class<K> key, Class<V> val) {
        if (str == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(str, getCollectionType(List.class, key, val));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode: " + e.getMessage(), e);
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

}
