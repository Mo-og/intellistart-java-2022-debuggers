package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseBad;
import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intellias.intellistart.interviewplanning.SpringSecurityTestUtils;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = SpringSecurityTestUtils.class
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  private static final String coordinatorEmail = "coordinator@test.com";
  private static final User coordinator = new User("coordinator@test.com", UserRole.COORDINATOR);
  private static final String email = "test.user@gmail.com";
  private static final User testCandidate = new User(email, UserRole.CANDIDATE);
  private static final User testCoordinator = new User(email, UserRole.COORDINATOR);
  private static final User testInterviewer = new User(email, UserRole.INTERVIEWER);

  static {
    testCandidate.setId(1L);
    testInterviewer.setId(1L);
    coordinator.setId(2L);
  }

  @MockBean
  private CommandLineRunner commandLineRunner;
  @MockBean
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private InterviewerService interviewerService;
  @MockBean
  private CoordinatorService coordinatorService;
  @MockBean
  private UserService userService;

  @Test
  void testCreateInterviewer() {
    when(userService.create(email, UserRole.INTERVIEWER)).thenReturn(testInterviewer);
    checkResponseOk(post("/interviewers"), json(email), json(testInterviewer),
        mockMvc);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(userService).create(captor.capture(), eq(UserRole.INTERVIEWER));
    assertThat(captor.getValue()).isEqualTo(email);
  }

  @Test
  void testGetInterviewer() {
    when(interviewerService.getById(1L)).thenReturn(testInterviewer);
    checkResponseOk(get("/interviewers/{interviewerId}", 1),
        null, json(testInterviewer), mockMvc);
  }

  @Test
  @WithUserDetails(coordinatorEmail)
  void testGrantInterviewerRole() throws Exception {
    when(coordinatorService.grantInterviewerRole(email, coordinatorEmail))
        .thenReturn(testInterviewer);
    mockMvc.perform(post("/users/interviewers")
            .content("{\n"
                + "\"email\": \"test.user@gmail.com\"\n"
                + "}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(json(testInterviewer)));
  }

  @Test
  @WithUserDetails(coordinatorEmail)
  void testSelfGrantInterviewerRole() throws Exception {
    when(coordinatorService.grantInterviewerRole(coordinatorEmail, coordinatorEmail))
        .thenThrow(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING,
            "Can not grant another role for yourself"));
    mockMvc.perform(post("/users/interviewers")
            .content("{\n"
                + "\"email\": \"coordinator@test.com\"\n"
                + "}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void testRevokeInterviewerRole() {
    when(coordinatorService.revokeInterviewerRole(1L)).thenReturn(testInterviewer);
    checkResponseOk(delete("/users/interviewers/{interviewerId}", 1L),
        null, json(testInterviewer), mockMvc);
  }

  @Test
  void testGetInterviewers() {
    when(coordinatorService.getUsersWithRole(UserRole.INTERVIEWER))
        .thenReturn(List.of(testInterviewer));
    checkResponseOk(get("/users/interviewers"),
        null, json(List.of(testInterviewer)), mockMvc);
  }

  @Test
  void testGrantCoordinatorRole() {
    when(coordinatorService.grantCoordinatorRole(email)).thenReturn(testCoordinator);
    checkResponseOk(post("/users/coordinators"),
        json(email), json(testCoordinator), mockMvc);
  }

  @Test
  @WithUserDetails(coordinatorEmail)
  void testRevokeCoordinatorRole() throws Exception {
    when(userService.loadUserByUsername(coordinatorEmail))
        .thenReturn(coordinator);
    when(coordinatorService.revokeCoordinatorRole(1L, 2L))
        .thenReturn(testCoordinator);
    this.mockMvc.perform(delete("/users/coordinators/{coordinatorId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(json(testCoordinator)));
  }

  @Test
  @WithUserDetails(coordinatorEmail)
  void testSelfRevokeCoordinatorRole() throws Exception {
    when(userService.loadUserByUsername(coordinatorEmail))
        .thenReturn(coordinator);
    when(coordinatorService.revokeCoordinatorRole(2L, 2L))
        .thenThrow(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING,
            "Can not revoke role for yourself"));
    this.mockMvc.perform(delete("/users/coordinators/{coordinatorId}", 2)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetCoordinators() {
    when(coordinatorService.getUsersWithRole(UserRole.COORDINATOR))
        .thenReturn(List.of(testCoordinator));
    checkResponseOk(get("/users/coordinators"),
        null, json(List.of(testCoordinator)), mockMvc);
  }

  @Test
  void testGetNonExistingInterviewer() {
    when(interviewerService.getById(-1L)).thenThrow(NotFoundException.interviewer(-1L));
    checkResponseBad(get("/interviewers/{id}", -1L),
        null, json(NotFoundException.interviewer(-1L)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testUnexpectedExceptionHandledByGlobalExceptionHandler() {
    checkResponseBad(post("/interviewers"),
        null, null,
        status().is5xxServerError(), mockMvc);
  }

  @Test
  void testGetUser() {
    when(userService.getById(1L)).thenReturn(testCoordinator);
    checkResponseOk(get("/users/{id}", 1),
        null, json(testCoordinator), mockMvc);
  }
}
