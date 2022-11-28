package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.utils.TestUtils.checkResponseBad;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils;
import com.intellias.intellistart.interviewplanning.utils.WithCustomUser;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc
@WithCustomUser
class UserControllerTest {

  private static final String EMAIL = "user@test.com";
  private static final User TEST_COORDINATOR = new User(EMAIL, UserRole.COORDINATOR);
  private static final User TEST_INTERVIEWER = new User(EMAIL, UserRole.INTERVIEWER);
  private static final User TEST_CANDIDATE = new User(EMAIL, UserRole.CANDIDATE);

  static {
    TEST_COORDINATOR.setId(1L);
    TEST_INTERVIEWER.setId(1L);
    TEST_CANDIDATE.setId(1L);
  }

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private InterviewerService interviewerService;
  @MockBean
  private CoordinatorService coordinatorService;

  @Test
  void testGetUserInfo() {
    User coordinator = new User(TestSecurityUtils.COORDINATOR_EMAIL, UserRole.COORDINATOR);
    coordinator.setId(TestSecurityUtils.COORDINATOR_ID);
    checkResponseOk(get("/me"), null, json(coordinator), mockMvc);
  }

  @Test
  void testGrantInterviewerRole() {
    when(coordinatorService.grantInterviewerRole(EMAIL, TestSecurityUtils.COORDINATOR_EMAIL))
        .thenReturn(TEST_INTERVIEWER);
    checkResponseOk(post("/users/interviewers"),
        json(EMAIL), json(TEST_INTERVIEWER), mockMvc);
  }

  @Test
  void testSelfGrantInterviewerRole() {
    when(coordinatorService.grantInterviewerRole(
        TestSecurityUtils.COORDINATOR_EMAIL,
        TestSecurityUtils.COORDINATOR_EMAIL))
        .thenThrow(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING));
    checkResponseBad(post("/users/interviewers"),
        json(TestSecurityUtils.COORDINATOR_EMAIL),
        json(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING)),
        status().isForbidden(), mockMvc);
  }

  @Test
  void testRevokeInterviewerRole() {
    when(coordinatorService.revokeInterviewerRole(1L)).thenReturn(TEST_CANDIDATE);
    checkResponseOk(delete("/users/interviewers/{interviewerId}", 1L),
        null, json(TEST_CANDIDATE), mockMvc);
  }

  @Test
  void testRevokeInterviewerWithWrongId() {
    when(coordinatorService.revokeInterviewerRole(-1L))
        .thenThrow(NotFoundException.user(-1L));
    checkResponseBad(delete("/users/interviewers/{interviewerId}", -1L),
        null, json(NotFoundException.user(-1L)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testRevokeInterviewerWithWrongRole() {
    when(coordinatorService.revokeInterviewerRole(2L))
        .thenThrow(NotFoundException.interviewer(2L));
    checkResponseBad(delete("/users/interviewers/{interviewerId}", 2L),
        null, json(NotFoundException.interviewer(2L)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testRevokeInterviewerWithActiveSlot() {
    when(coordinatorService.revokeInterviewerRole(1L))
        .thenThrow(new ApplicationErrorException(ErrorCode.REVOKE_USER_WITH_SLOT));
    checkResponseBad(delete("/users/interviewers/{interviewerId}", 1L),
        null, json(new ApplicationErrorException(ErrorCode.REVOKE_USER_WITH_SLOT)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testGetInterviewers() {
    when(coordinatorService.getUsersWithRole(UserRole.INTERVIEWER))
        .thenReturn(List.of(TEST_INTERVIEWER));
    checkResponseOk(get("/users/interviewers"),
        null, json(List.of(TEST_INTERVIEWER)), mockMvc);
  }

  @Test
  void testGrantCoordinatorRole() {
    when(coordinatorService.grantCoordinatorRole(EMAIL)).thenReturn(TEST_COORDINATOR);
    checkResponseOk(post("/users/coordinators"),
        json(EMAIL), json(TEST_COORDINATOR), mockMvc);
  }

  @Test
  void testGrantCoordinatorWithActiveInterviewerSlot() {
    when(coordinatorService.grantCoordinatorRole(EMAIL))
        .thenThrow(new ApplicationErrorException(ErrorCode.REVOKE_USER_WITH_SLOT));
    checkResponseBad(post("/users/coordinators"),
        json(EMAIL), json(new ApplicationErrorException(ErrorCode.REVOKE_USER_WITH_SLOT)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(coordinatorService.revokeCoordinatorRole(2L, 1L))
        .thenReturn(TEST_CANDIDATE);
    checkResponseOk(delete("/users/coordinators/{coordinatorId}", 2L),
        null, json(TEST_CANDIDATE), mockMvc);
  }

  @Test
  void testSelfRevokeCoordinatorRole() {
    when(coordinatorService.revokeCoordinatorRole(1L, 1L))
        .thenThrow(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING));
    checkResponseBad(delete("/users/coordinators/{coordinatorId}", 1L),
        null, json(new ApplicationErrorException(ErrorCode.SELF_ROLE_REVOKING)),
        status().isForbidden(), mockMvc);
  }

  @Test
  void testRevokeCoordinatorWithWrongId() {
    when(coordinatorService.revokeCoordinatorRole(-1L, 1L))
        .thenThrow(NotFoundException.user(-1L));
    checkResponseBad(delete("/users/coordinators/{coordinatorId}", -1L),
        null, json(NotFoundException.user(-1L)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testRevokeCoordinatorWithWrongRole() {
    when(coordinatorService.revokeCoordinatorRole(2L, 1L))
        .thenThrow(NotFoundException.coordinator(2L));
    checkResponseBad(delete("/users/coordinators/{coordinatorId}", 2L),
        null, json(NotFoundException.coordinator(2L)),
        status().is4xxClientError(), mockMvc);
  }

  @Test
  void testGetCoordinators() {
    when(coordinatorService.getUsersWithRole(UserRole.COORDINATOR))
        .thenReturn(List.of(TEST_COORDINATOR));
    checkResponseOk(get("/users/coordinators"),
        null, json(List.of(TEST_COORDINATOR)), mockMvc);
  }
}
