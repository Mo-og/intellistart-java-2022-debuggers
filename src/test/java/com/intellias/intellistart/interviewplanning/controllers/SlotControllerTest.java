package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.CANDIDATE_EMAIL;
import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.INTERVIEWER_ID;
import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.interviewer;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseBad;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.json;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils;
import com.intellias.intellistart.interviewplanning.test_utils.WithCustomUser;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc
@WithCustomUser
class SlotControllerTest {

  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("08:00", "10:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(CANDIDATE_EMAIL, "2022-11-03", "08:00", "10:00");
  private static final BookingDto bookingDto = BookingDto.builder()
      .from(LocalTime.of(8, 0)).to(LocalTime.of(10, 0))
      .subject("some subject").description("some desc")
      .interviewerSlotId(interviewerSlot.getId())
      .candidateSlotId(candidateSlot.getId())
      .build();

  private final WeekService actualWeekService = new WeekServiceImp();

  @BeforeAll
  static void setupSlots() {
    interviewer.setId(INTERVIEWER_ID);
    interviewerSlot.setId(1L);
    interviewerSlot.setInterviewer(TestSecurityUtils.interviewer);
    candidateSlot.setId(1L);
  }

  private final CandidateSlotDto candidateSlotDto =
      new CandidateSlotDto(candidateSlot.getId(), LocalTime.parse("08:00"),
          LocalTime.parse("10:00"), actualWeekService.getCurrentDate(), List.of(bookingDto));

  private final InterviewerSlotDto interviewerSlotDto1 =
      new InterviewerSlotDto(INTERVIEWER_ID, actualWeekService.getCurrentWeekNum(),
          "friday", LocalTime.parse("08:00"),
          LocalTime.parse("10:00"), List.of(bookingDto));
  private final InterviewerSlotDto interviewerSlotDto2 =
      new InterviewerSlotDto(INTERVIEWER_ID, actualWeekService.getNextWeekNum(),
          "friday", LocalTime.parse("08:00"),
          LocalTime.parse("10:00"), List.of(bookingDto));

  @Autowired
  private MockMvc mockMvc;
  @SpyBean
  private InterviewerService interviewerService;
  @MockBean
  private CandidateService candidateService;

  @Test
  void testGetAllInterviewerSlots() {
    doReturn(List.of(interviewerSlotDto1)).when(interviewerService).getRelevantInterviewerSlots(1L);
    checkResponseOk(
        get("/interviewers/{INTERVIEWER_ID}/slots", 1L),
        null, json(List.of(interviewerSlotDto1)), mockMvc);
  }

  @Test
  void testAddSlotToInterviewer() {
    doReturn(interviewerSlotDto1).when(interviewerService).createSlot(1L, interviewerSlotDto1);
    checkResponseOk(
        post("/interviewers/{INTERVIEWER_ID}/slots", 1L),
        json(interviewerSlotDto1), json(interviewerSlotDto1), mockMvc);
  }

  @Test
  void testUpdateInterviewerTimeSlot() {
    doReturn(interviewerSlotDto1).when(interviewerService).updateSlot(1L, 1L, interviewerSlotDto1);
    checkResponseOk(
        post("/interviewers/{INTERVIEWER_ID}/slots/{slotId}", 1L, 1L),
        json(interviewerSlotDto1), json(interviewerSlotDto1), mockMvc);
  }

  @Test
  @WithCustomUser(CANDIDATE_EMAIL)
  void testUpdateCandidateTimeSlot() {
    when(candidateService
        .updateSlot(CANDIDATE_EMAIL, 1L, candidateSlotDto))
        .thenReturn(candidateSlotDto);
    checkResponseOk(
        post("/candidates/current/slots/{slotId}", 1L),
        json(candidateSlotDto), json(candidateSlotDto), mockMvc);
  }

  @Test
  void testGetCurrentWeekInterviewerSlots() {
    when(interviewerService.getSlotsByWeekId(INTERVIEWER_ID,
        actualWeekService.getCurrentWeekNum()))
        .thenReturn(List.of(interviewerSlotDto1));
    checkResponseOk(
        get("/interviewers/{INTERVIEWER_ID}/slots/weeks/current", INTERVIEWER_ID),
        null, json(List.of(interviewerSlotDto1)), mockMvc);
  }

  @Test
  void testGetNextWeekInterviewerSlots() {
    when(interviewerService.getSlotsByWeekId(INTERVIEWER_ID,
        actualWeekService.getNextWeekNum()))
        .thenReturn(List.of(interviewerSlotDto2));

    checkResponseOk(
        get("/interviewers/{INTERVIEWER_ID}/slots/weeks/next", INTERVIEWER_ID),
        null, json(List.of(interviewerSlotDto2)), mockMvc);
  }

  @Test
  void testDeleteSlots() {
    doReturn(interviewer).when(interviewerService).getInterviewerById(INTERVIEWER_ID);
    doReturn(interviewerSlot).when(interviewerService)
        .getSlotById(INTERVIEWER_ID, interviewerSlot.getId());

    checkResponseOk(
        delete("/interviewers/{INTERVIEWER_ID}/slots/{slotId}",
            INTERVIEWER_ID, interviewerSlot.getId()),
        null, null, mockMvc);
  }

  @Test
  @WithCustomUser(TestSecurityUtils.INTERVIEWER_EMAIL)
  void testDeleteSlotsOfAnotherUserNoPermission() {
    doReturn(interviewerSlot).when(interviewerService)
        .getSlotById(INTERVIEWER_ID, interviewerSlot.getId());

    checkResponseBad(
        delete("/interviewers/{INTERVIEWER_ID}/slots/{slotId}",
            INTERVIEWER_ID + 2, interviewerSlot.getId()),
        null, null, status().isForbidden(), mockMvc);
  }

  @Test
  void testDeleteSlotsSlotBoundedToAnotherUser() {
    doReturn(interviewerSlot).when(interviewerService)
        .getSlotById(INTERVIEWER_ID, interviewerSlot.getId());

    checkResponseBad(
        delete("/interviewers/{INTERVIEWER_ID}/slots/{slotId}",
            INTERVIEWER_ID + 2, interviewerSlot.getId()),
        null, null, status().isNotFound(), mockMvc);
  }
}
