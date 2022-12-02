package com.intellias.intellistart.interviewplanning.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public abstract class TestUtils {

  private static final ObjectWriter jsonWriter;
  public static boolean debug = true;

  static {
    jsonWriter = new ObjectMapper()
        .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        .writer()
        .withDefaultPrettyPrinter();
  }

  @SneakyThrows
  public static String json(Object o) {
    return jsonWriter.writeValueAsString(o);
  }

  @SneakyThrows
  public static String checkResponseOk(MockHttpServletRequestBuilder methodAndUrl, String requestBody,
      String responseBody, MockMvc mock) {
    return checkResponseBad(methodAndUrl, requestBody, responseBody, status().isOk(), mock);
  }

  @SneakyThrows
  public static String checkResponseBad(MockHttpServletRequestBuilder methodAndUrl, String requestBody,
      String responseBody, ResultMatcher resultMatcher, MockMvc mock) {
    methodAndUrl
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(StandardCharsets.UTF_8);
    if (requestBody != null) {
      methodAndUrl = methodAndUrl.content(requestBody);
    }
    var result = mock.perform(methodAndUrl);
    if (debug) {
      result.andDo(print());
    }
    result.andExpect(resultMatcher);
    if (responseBody != null) {
      result.andExpect(content().json(responseBody));
    }
    return result.andReturn().getResponse().getContentAsString();
  }
}
