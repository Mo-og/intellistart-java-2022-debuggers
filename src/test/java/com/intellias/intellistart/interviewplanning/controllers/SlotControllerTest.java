package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class SlotControllerTest {

  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("08:00", "10:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(CANDIDATE_EMAIL, "2022-11-03", "08:00", "10:00");

  static {
    interviewerSlot.setId(1L);
    candidateSlot.setId(1L);
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
  private CandidateService candidateService;

  @Test
  void testGetAllInterviewerSlots() {
    when(interviewerService
        .getRelevantInterviewerSlots(1L))
        .thenReturn(List.of(interviewerSlot));
    checkResponseOk(
        get("/interviewers/{interviewerId}/slots", 1L),
        null, json(List.of(interviewerSlot)), mockMvc);
  }

  @Test
  void testAddSlotToInterviewer() {
    when(interviewerService
        .createSlot(1L, interviewerSlot))
        .thenReturn(interviewerSlot);
    checkResponseOk(
        post("/interviewers/{interviewerId}/slots", 1L),
        json(interviewerSlot), json(interviewerSlot), mockMvc);
  }

  @Test
  void testUpdateInterviewerTimeSlot() {
    when(interviewerService
        .updateSlot(1L, 1L, interviewerSlot))
        .thenReturn(interviewerSlot);
    checkResponseOk(
        post("/interviewers/{interviewerId}/slots/{slotId}", 1L, 1L),
        json(interviewerSlot), json(interviewerSlot), mockMvc);
  }

  @Test
  void testUpdateCandidateTimeSlot() {
    when(candidateService
        .updateSlot(1L, candidateSlot))
        .thenReturn(candidateSlot);
    checkResponseOk(
        post("/candidates/current/slots/{slotId}", 1L),
        json(candidateSlot), json(candidateSlot), mockMvc);
  }
}
