package com.jsoft.magenta.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtils {

  public static String asJsonString(Object obj) {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = null;
    try {
      jsonString = mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("Error during object to json mapping");
      throw new IllegalStateException("Error during object to json mapping");
    }
    return jsonString;
  }
}
