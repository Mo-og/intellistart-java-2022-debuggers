package com.intellias.intellistart.interviewplanning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intellias.intellistart.interviewplanning.controllers.UserController;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class InterviewerControllerTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectWriter jsonWriter;

  static {
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    jsonWriter = mapper.writer().withDefaultPrettyPrinter();
  }

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private InterviewerService interviewerService;
  @MockBean
  private CoordinatorService coordinatorService;
  @MockBean
  private UserService userService;

  @SneakyThrows
  private static String json(Object o) {
    return jsonWriter.writeValueAsString(o);
  }

  @Test
  void testCreateUser() throws Exception {
    this.mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(json("test.user@gmail.com")))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void testCreateInterviewer() throws Exception {
    UserService serviceMock = mock(UserService.class, RETURNS_DEFAULTS);
    String email = "test.interviewer@gmail.com";
    User mockUser = new User(email, UserRole.INTERVIEWER);
    when(serviceMock.create(email, UserRole.INTERVIEWER)).thenReturn(mockUser);

    this.mockMvc.perform(post("/interviewers")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8)
            .content(json(email)))
        .andDo(print())
        .andExpect(status().isOk());

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(userService).create(captor.capture(), eq(UserRole.INTERVIEWER));
    assertThat(captor.getValue()).isEqualTo(email);
  }

  @Test
  void testGetInterviewer() throws Exception {
    this.mockMvc.perform(get("/interviewers/{interviewerId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding(StandardCharsets.UTF_8))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
