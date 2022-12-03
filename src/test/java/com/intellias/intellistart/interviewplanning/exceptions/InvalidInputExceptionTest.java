package com.intellias.intellistart.interviewplanning.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;

class InvalidInputExceptionTest {

  private static InvalidInputException exception;

  @Test
  void invalidRoundingTest() {
    exception = InvalidInputException.minutes();
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
      assertEquals("Invalid time boundaries: minutes should be rounded to 00 or 30 minutes",
          e.getMessage());
    }
  }

  @Test
  void invalidTimeUpperBoundTest() {
    exception = InvalidInputException.timeUpperBound();
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
      assertEquals("Invalid time boundaries: end time cannot be after 22:00",
          e.getMessage());
    }
  }

  @Test
  void invalidTimeLowerBoundTest() {
    exception = InvalidInputException.timeLowerBound();
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
      assertEquals("Invalid time boundaries: start time cannot be before 08:00",
          e.getMessage());
    }
  }

  @Test
  void invalidMinPeriodTest() {
    exception = InvalidInputException.period();
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
      assertEquals("Invalid time boundaries: period cannot be less than 1.5h",
          e.getMessage());
    }
  }

  @ Test
  void periodIntersectionTest() {
   exception = InvalidInputException.periodOverlapping();
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.SLOT_IS_OVERLAPPING.code, e.getErrorCode());
      assertEquals("Slot overlaps another one by time: slot at this time already exists",
          e.getMessage());
    }
  }

  @Test
  void invalidDayOfWeekTestTest() {
    exception = InvalidInputException.weekNum(202201);
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_WEEK_NUM.code, e.getErrorCode());
      assertEquals("Invalid week number: cannot create or edit booking limit on week '202201'",
          e.getMessage());
    }
  }

  @Test
  void invalidWeekNumTest() {
    exception = InvalidInputException.dayOfWeek(DayOfWeek.FRIDAY);
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_DAY_OF_WEEK.code, e.getErrorCode());
      assertEquals("Invalid day of week: cannot create or edit slot on friday",
          e.getMessage());
    }
  }

  @Test
  void invalidBookingLimitTest() {
    exception = InvalidInputException.bookingLimit(5, 3);
    try {
      throw exception;
    } catch (InvalidInputException e) {
      assertEquals(ErrorCode.INVALID_BOOKING_LIMIT.code, e.getErrorCode());
      assertEquals(
          "Invalid booking limit number: booking limit '5' cannot be lower than the number of existing bookings '3'",
          e.getMessage());
    }
  }
}
