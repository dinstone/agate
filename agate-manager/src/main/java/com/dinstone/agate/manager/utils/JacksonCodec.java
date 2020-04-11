package com.dinstone.agate.manager.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
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
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("Failed to encode as JSON: " + e.getMessage(), e);
		}
	}

	public static <T> T decodeValue(String str, Class<T> clazz) {
		try {
			return objectMapper.readValue(str, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Failed to decode: " + e.getMessage(), e);
		}
	}

}
