package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.json;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils;
import com.intellias.intellistart.interviewplanning.test_utils.WithCustomUser;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc
class BookingLimitControllerTest {

  private static final int limit = 5;
  private static final WeekService weekService = new WeekServiceImp();
  private static final int nextWeekNum = weekService.getNextWeekNum();
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

  @Test
  @WithCustomUser
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
            InvalidInputException.weekNum(weekService.getCurrentWeekNum()));
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
  @WithCustomUser
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
  @WithCustomUser
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