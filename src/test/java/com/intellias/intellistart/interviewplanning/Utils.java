package com.intellias.intellistart.interviewplanning;

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

public abstract class Utils {

  private static final ObjectWriter jsonWriter;

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
  public static void checkResponseOk(MockHttpServletRequestBuilder methodAndUrl, String requestBody,
      String responseBody, MockMvc mock) {
    checkResponseBad(methodAndUrl, requestBody, responseBody, status().isOk(), mock);
  }

  public static void checkResponseBad(MockHttpServletRequestBuilder methodAndUrl,
      String requestBody,
      String responseBody, ResultMatcher resultMatcher, MockMvc mock)
      throws Exception {
    methodAndUrl
        .contentType(MediaType.APPLICATION_JSON)
        .characterEncoding(StandardCharsets.UTF_8);
    if (requestBody != null) {
      methodAndUrl = methodAndUrl.content(requestBody);
    }
    var result = mock.perform(methodAndUrl)
        .andDo(print())
        .andExpect(resultMatcher);
    if (responseBody != null) {
      result.andExpect(content().json(responseBody));
    }
  }
}
