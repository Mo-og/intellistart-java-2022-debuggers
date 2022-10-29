package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekEditException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.dto.BookingLimitResponse;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingLimitController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingLimitControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookingLimitService bookingLimitService;

  private static final int limit = 5;
  private static final int currentWeekNum = WeekService.getCurrentWeekNum();
  private static final int nextWeekNum = WeekService.getNextWeekNum();
  private static final Long existingUserId = 1L;
  private static final Long notExistingUserId = 2L;

  private static final BookingLimit bookingLimit = new BookingLimit(
      existingUserId,
      nextWeekNum,
      limit);
  private static final BookingLimit bookingLimit2 = new BookingLimit(
      notExistingUserId,
      nextWeekNum,
      limit + 1);
  private static final BookingLimitResponse bookingLimitResponseDTO
      = new BookingLimitResponse(10);

  @Test
  void testSetBookingLimit() {
    when(bookingLimitService.setBookingLimit(limit, existingUserId, nextWeekNum))
        .thenReturn(bookingLimit);
    checkResponseOk(
        post("/interviewers/current/bookingLimits")
            .param("bookingLimit", "5")
            .param("interviewerId", "1")
            .param("weekNum", "202244"),
        null,
        json(bookingLimit),
        this.mockMvc);
  }

  @Test
  void testSetBookingLimitWeekException() {
    when(bookingLimitService.setBookingLimit(limit, existingUserId, currentWeekNum))
        .thenThrow(new WeekEditException(existingUserId + ""));
    assertThrows(WeekEditException.class,
        () -> bookingLimitService.setBookingLimit(limit, existingUserId, currentWeekNum));
  }

  @Test
  void testSetBookingLimitUserException() {
    when(bookingLimitService.setBookingLimit(limit, notExistingUserId, nextWeekNum))
        .thenThrow(new UserNotFoundException(notExistingUserId + ""));
    assertThrows(UserNotFoundException.class,
        () -> bookingLimitService.setBookingLimit(limit, notExistingUserId, nextWeekNum));
  }

  @Test
  void testGetWeekBookingLimits() {
    when(bookingLimitService.getWeekBookingLimits(nextWeekNum))
        .thenReturn(List.of(bookingLimit, bookingLimit2));
    checkResponseOk(
        get("/interviewers/bookingLimits/week")
            .param("weekNum", "202244"),
        null,
        json(List.of(bookingLimit, bookingLimit2)),
        this.mockMvc);
  }

  @Test
  void testGetBookingLimit() {
    when(bookingLimitService.getBookingLimit(existingUserId, nextWeekNum))
        .thenReturn(bookingLimit);
    checkResponseOk(
        get("/interviewers/bookingLimits/week/user")
            .param("interviewerId", "1")
            .param("weekNum", "202244"),
        null,
        json(bookingLimit),
        this.mockMvc);
  }

  @Test
  void testGetUserWeekBookingLimit() {
    when(bookingLimitService.getUserWeekBookingLimit(existingUserId, nextWeekNum))
        .thenReturn(bookingLimitResponseDTO);
    checkResponseOk(
        get("/interviewers/bookingLimits/week/user/limit")
            .param("interviewerId", "1")
            .param("weekNum", "202244"),
        null,
        json(bookingLimitResponseDTO),
        this.mockMvc);
  }
}