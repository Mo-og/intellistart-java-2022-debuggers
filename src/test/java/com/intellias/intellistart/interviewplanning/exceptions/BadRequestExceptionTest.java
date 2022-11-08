package com.intellias.intellistart.interviewplanning.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

  private static BadRequestException exception;

  @Test
  void invalidBoundariesTest() {
    exception = BadRequestException.invalidBoundaries("18:00", "9:00");
    try {
      throw exception;
    } catch (BadRequestException e) {
      assertEquals("Invalid time boundaries: 18:00 - 9:00", e.getMessage());
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
    }
  }

  @Test
  void invalidDayOfWeekTest() {
    exception = BadRequestException.invalidDayOfWeek(DayOfWeek.FRIDAY);
    try {
      throw exception;
    } catch (BadRequestException e) {
      assertEquals(ErrorCode.INVALID_DAY_OF_WEEK.code, e.getErrorCode());
      assertEquals("Invalid day of week: cannot create or edit slot on friday",
          e.getMessage());
    }
  }

  @Test
  void invalidBookingLimitTest() {
    exception = BadRequestException.invalidBookingLimit(5, 3);
    try {
      throw exception;
    } catch (BadRequestException e) {
      assertEquals(ErrorCode.INVALID_BOOKING_LIMIT.code, e.getErrorCode());
      assertEquals(
          "Invalid booking limit number: booking limit \"5\" cannot be lower than the number of existing bookings \"3\"",
          e.getMessage());
    }
  }
}
