package com.jsoft.magenta.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Stringify {

  public final static String BASE_URL = "http://localhost:8080/magenta/v1/";

  public static String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonContent = mapper.writeValueAsString(obj);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
