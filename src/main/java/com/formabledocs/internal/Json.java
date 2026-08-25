package com.formabledocs.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Json {
  static final ObjectMapper MAPPER =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);

  private Json() {}

  public static String write(Object value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize request body", e);
    }
  }

  static <T> T read(String text, Class<T> type) {
    try {
      return MAPPER.readValue(text, type);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to parse Formable API response", e);
    }
  }

  static <T> T read(String text, TypeReference<T> type) {
    try {
      return MAPPER.readValue(text, type);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to parse Formable API response", e);
    }
  }

  static Object parse(String text) {
    if (text == null || text.isBlank()) {
      return null;
    }
    try {
      return MAPPER.readValue(text, Object.class);
    } catch (JsonProcessingException e) {
      return text;
    }
  }
}
