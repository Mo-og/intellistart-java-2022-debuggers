package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.json;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils;
import com.intellias.intellistart.interviewplanning.test_utils.WithCustomUser;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc(addFilters = false)
@WithCustomUser
class BookingControllerTest {

  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(1L)
          .candidateSlotId(1L)
          .build();

  static {
    bookingDto.setId(1L);
  }

  @MockBean
  private CommandLineRunner commandLineRunner;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookingService bookingService;

  @Test
  void testCreateBooking() {
    when(bookingService
        .createBooking(bookingDto))
        .thenReturn(bookingDto);
    checkResponseOk(
        post("/bookings"),
        json(bookingDto), json(bookingDto), mockMvc);
  }

  @Test
  void testUpdateBooking() {
    when(bookingService
        .updateBooking(1L, bookingDto))
        .thenReturn(bookingDto);
    checkResponseOk(
        post("/bookings/{bookingId}", 1L),
        json(bookingDto), json(bookingDto), mockMvc);
  }

  @Test
  void testDeleteBooking() {
    checkResponseOk(
        delete("/bookings/{bookingId}", 1L),
        null, null, mockMvc);
  }
}
