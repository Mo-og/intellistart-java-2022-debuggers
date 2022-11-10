package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.TestUtils.json;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.models.dto.BookingLimitRequest;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookingLimitController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingLimitControllerTest {

  private static final UserDetails user = new User("test.user@test.com", UserRole.COORDINATOR);
  private static final int limit = 5;
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
  private static final BookingLimitDto bookingLimitRequest = new BookingLimitDto(limit,
      nextWeekNum);
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookingLimitService bookingLimitService;
  @MockBean
  private CommandLineRunner commandLineRunner;
  @MockBean
  private JwtRequestFilter jwtRequestFilter;

  @Test
  void testSetBookingLimit() {
    when(bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest))
        .thenReturn(bookingLimit);
    checkResponseOk(
        post("/interviewers/{interviewerId}/bookingLimits", existingUserId),
        json(bookingLimitRequest),
        json(bookingLimit),
        this.mockMvc);
  }

  @Test
  void testSetBookingLimitWeekException() {
    when(bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest))
        .thenThrow(
            InvalidInputException.weekNum(WeekService.getCurrentWeekNum()));
    assertThrows(ApplicationErrorException.class,
        () -> bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest));
  }

  @Test
  void testSetBookingLimitUserException() {
    when(bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest))
        .thenThrow(NotFoundException.interviewer(existingUserId));
    assertThrows(ApplicationErrorException.class,
        () -> bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest));
  }

  @Test
  void testGetWeekBookingLimits() {
    when(bookingLimitService.getBookingLimitsByWeekNum(nextWeekNum))
        .thenReturn(List.of(bookingLimit, bookingLimit2));
    checkResponseOk(
        get("/interviewers/bookingLimits/{weekNum}", nextWeekNum),
        null,
        json(List.of(bookingLimit, bookingLimit2)),
        this.mockMvc);
  }

  @Test
  void testGetBookingLimit() {
    when(bookingLimitService.findBookingLimit(existingUserId, nextWeekNum))
        .thenReturn(bookingLimit);
    checkResponseOk(
        get("/interviewers/{interviewerId}/bookingLimits/{weekNum}", existingUserId, nextWeekNum)
            .param("interviewerId", "1")
            .param("weekNum", "202244"),
        null,
        json(bookingLimit),
        this.mockMvc);
  }
}