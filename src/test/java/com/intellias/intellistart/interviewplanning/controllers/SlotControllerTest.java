package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.CANDIDATE_EMAIL;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils;
import com.intellias.intellistart.interviewplanning.utils.WithCustomUser;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
  private static final Long interviewerId = 1L;
  @SpyBean
  private WeekServiceImp weekService;
  private final WeekService actualWeekService = new WeekServiceImp();
  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(interviewerSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();
  private final InterviewerSlotDto interviewerSlotDto1 =
      new InterviewerSlotDto(interviewerId, actualWeekService.getCurrentWeekNum(),
          "friday", LocalTime.parse("08:00"),
          LocalTime.parse("10:00"), List.of(bookingDto));
  private final InterviewerSlotDto interviewerSlotDto2 =
      new InterviewerSlotDto(interviewerId, actualWeekService.getNextWeekNum(),
          "friday", LocalTime.parse("08:00"),
          LocalTime.parse("10:00"), List.of(bookingDto));

  static {
    interviewerSlot.setId(1L);
    candidateSlot.setId(1L);
  }

  @MockBean
  private CommandLineRunner commandLineRunner;
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
  @WithCustomUser(CANDIDATE_EMAIL)
  void testUpdateCandidateTimeSlot() {
    when(candidateService
        .updateSlot(1L, candidateSlot))
        .thenReturn(candidateSlot);
    checkResponseOk(
        post("/candidates/current/slots/{slotId}", 1L),
        json(candidateSlot), json(candidateSlot), mockMvc);
  }

  @Test
  void testGetCurrentWeekInterviewerSlots() {
    when(interviewerService.getSlotsByWeekId(interviewerId,
        actualWeekService.getCurrentWeekNum()))
        .thenReturn(List.of(interviewerSlotDto1));
    checkResponseOk(
        get("/interviewers/{interviewerId}/slots/weeks/current", interviewerId),
        null, json(List.of(interviewerSlotDto1)), mockMvc);
  }

  @Test
  void testGetNextWeekInterviewerSlots() {
    when(interviewerService.getSlotsByWeekId(interviewerId,
        actualWeekService.getNextWeekNum()))
        .thenReturn(List.of(interviewerSlotDto2));
    System.out.println(interviewerSlotDto2);
    checkResponseOk(
        get("/interviewers/{interviewerId}/slots/weeks/next", interviewerId),
        null, json(List.of(interviewerSlotDto2)), mockMvc);
  }
}
