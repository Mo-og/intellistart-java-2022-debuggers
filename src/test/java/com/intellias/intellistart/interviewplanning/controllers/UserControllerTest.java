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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private InterviewerService interviewerService;
  @MockBean
  private CoordinatorService coordinatorService;
  @MockBean
  private UserService userService;
  private static final String email = "test.user@gmail.com";
  private static final User testCandidate = new User(email, UserRole.CANDIDATE);
  private static final User testCoordinator = new User(email, UserRole.COORDINATOR);
  private static final User testInterviewer = new User(email, UserRole.INTERVIEWER);

  static {
    testCandidate.setId(1L);
    testInterviewer.setId(1L);
  }


  @Test
  void testCreateUser() {
    when(userService.create(email, UserRole.CANDIDATE)).thenReturn(testCandidate);
    checkResponseOk(post("/users"), json(email), json(testCandidate), mockMvc);
  }

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
  void testGrantInterviewerRole() {
    when(coordinatorService.grantRole(email, UserRole.INTERVIEWER)).thenReturn(testInterviewer);
    System.out.println("JSON: " + json(testInterviewer));
    checkResponseOk(post("/users/interviewers"), json(email), json(testInterviewer), mockMvc);
  }

  @Test
  void testRevokeInterviewerRole() {
    when(coordinatorService.revokeInterviewerRole(1L)).thenReturn(testCandidate);
    checkResponseOk(delete("/users/interviewers/{interviewerId}", 1L),
        null, json(testCandidate), mockMvc);
  }

  @Test
  void testGetInterviewers() {
    var set = new HashSet<User>();
    set.add(testInterviewer);
    when(coordinatorService.getUsersWithRole(UserRole.INTERVIEWER)).thenReturn(set);
    checkResponseOk(get("/users/interviewers"),
        null, json(set), mockMvc);
  }

  @Test
  void testGrantCoordinatorRole() {
    when(coordinatorService.grantRole(email, UserRole.COORDINATOR)).thenReturn(testCoordinator);
    checkResponseOk(post("/users/coordinators"),
        json(email), json(testCoordinator), mockMvc);
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(coordinatorService.revokeCoordinatorRole(1L)).thenReturn(testCandidate);
    checkResponseOk(delete("/users/coordinators/{coordinatorId}", 1),
        null, json(testCandidate), mockMvc);
  }

  @Test
  void testGetCoordinators() {
    var set = new HashSet<User>();
    set.add(testCoordinator);
    when(coordinatorService.getUsersWithRole(UserRole.COORDINATOR)).thenReturn(set);
    checkResponseOk(get("/users/coordinators"),
        null, json(set), mockMvc);
  }

  @Test
  void testGetNonExistingInterviewer() {
    when(interviewerService.getById(-1L)).thenThrow(new InterviewerNotFoundException(-1L));
    checkResponseBad(get("/interviewers/{id}", -1L),
        null, json(new InterviewerNotFoundException(-1L)),
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
    when(userService.getUserById(1L)).thenReturn(testCandidate);
    checkResponseOk(get("/users/{id}", 1),
        null, json(testCoordinator), mockMvc);
  }


}
