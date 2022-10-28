package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.Utils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.Utils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class SlotControllerTest {

  private static final InterviewerTimeSlot timeSlot =
      new InterviewerTimeSlot("08:00", "10:00", "WEDNESDAY", 202240);

  private static final CandidateTimeSlot candidateTimeSlot =
      new CandidateTimeSlot("2022-09-10", "08:00", "10:00");

  static {
    timeSlot.setId(1L);
    candidateTimeSlot.setId(2L);
  }

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private InterviewerService interviewerService;

  @MockBean
  private CandidateService candidateService;

  @Test
  void testPostInterviewerSlots() {
    when(interviewerService
        .createSlot(1L, timeSlot))
        .thenReturn(timeSlot);
    checkResponseOk(
        post("/interviewers/{interviewerId}/slots", 1L),
        json(timeSlot), json(timeSlot), this.mockMvc);
  }

  @Test
  void testGetInterviewerSlots() {
    var set = new HashSet<InterviewerTimeSlot>();
    set.add(timeSlot);
    when(interviewerService
        .getRelevantInterviewerSlots(1L))
        .thenReturn(set);
    checkResponseOk(
        get("/interviewers/{interviewerId}/slots", 1L),
        null, json(set), this.mockMvc);
  }
}
