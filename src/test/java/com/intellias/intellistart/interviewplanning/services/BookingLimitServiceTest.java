package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(BookingLimitService.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingLimitServiceTest {


  @MockBean
  private BookingLimitService bookingLimitService;

  private static final int limit = 5;
  private static final int nextWeekNum = WeekService.getNextWeekNum();
  private static final Long existingUserId = 1L;

  private static final BookingLimit bookingLimit = new BookingLimit(
      existingUserId,
      nextWeekNum,
      limit);

  @Test
  void testSetBooking() {
    when(bookingLimitService.setBookingLimit(limit, existingUserId, nextWeekNum))
        .thenReturn(bookingLimit);
    BookingLimit newBookingLimit = new BookingLimit(existingUserId, nextWeekNum, limit);
    assertEquals(bookingLimit.getId(), newBookingLimit.getId());
    assertEquals(bookingLimit.getInterviewerId(), newBookingLimit.getInterviewerId());
    assertEquals(bookingLimit.getBookingLimit(), newBookingLimit.getBookingLimit());


  }

}
