package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.Utils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.Utils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class SlotControllerTest {

  private static final InterviewerTimeSlot interviewerTimeSlot =
      new InterviewerTimeSlot("08:00", "10:00", "WEDNESDAY", 202240);

  static {
    interviewerTimeSlot.setId(1L);
  }

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private InterviewerService interviewerService;

  @Test
  void testInterviewerSlots() {
    when(interviewerService
        .createSlot(1L, interviewerTimeSlot))
        .thenReturn(interviewerTimeSlot);
    checkResponseOk(
        post("/interviewers/{interviewerId}/slots", 1L),
        json(interviewerTimeSlot), json(interviewerTimeSlot), this.mockMvc);
//    /interviewers/{interviewerId}/slots
  }
}
