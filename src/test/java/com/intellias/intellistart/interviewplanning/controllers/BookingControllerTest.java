package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("08:00", "18:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot("2022-11-03", "08:00", "13:00");
  private static final Booking booking =
      new Booking(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          candidateSlot,
          interviewerSlot,
          "some subject",
          "some desc"
      );

  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(interviewerSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();

  static {
    interviewerSlot.setId(1L);
  }

  @MockBean
  private CommandLineRunner commandLineRunner;
  @MockBean
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookingService bookingService;
  @MockBean
  private InterviewerService interviewerService;
  @MockBean
  private CandidateService candidateService;

  @Test
  void testCreateBooking() {
    when(bookingService
        .createBooking(bookingDto))
        .thenReturn(bookingDto);
    checkResponseOk(
        post("/bookings"),
        json(booking), json(booking), mockMvc);
  }

  @Test
  void testUpdateBooking() {
    when(bookingService
        .updateBooking(1L, booking))
        .thenReturn(booking);
    checkResponseOk(
        post("/bookings/{bookingId}", 1L),
        json(booking), json(booking), mockMvc);
  }

  @Test
  void testDeleteBooking() {
    checkResponseOk(
        delete("/bookings/{bookingId}", 1L),
        null, null, mockMvc);
  }
}
