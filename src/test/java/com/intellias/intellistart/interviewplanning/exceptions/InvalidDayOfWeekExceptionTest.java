package com.intellias.intellistart.interviewplanning.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;

class InvalidDayOfWeekExceptionTest {

  private static InvalidBoundariesException exception;

  @Test
  void testConstructorString() {
    exception = new InvalidBoundariesException("test");
    try {
      throw exception;
    } catch (InvalidBoundariesException e) {
      assertEquals("test", e.getMessage());
      assertEquals(ErrorCode.INVALID_BOUNDARIES.code, e.getErrorCode());
    }
  }

  @Test
  void testConstructorLong() {
    exception = new InvalidBoundariesException(DayOfWeek.FRIDAY);
    try {
      throw exception;
    } catch (InvalidBoundariesException e) {
      assertEquals(ErrorCode.INVALID_DAY_OF_WEEK.code, e.getErrorCode());
      assertEquals(
          String.format(InvalidBoundariesException.DAY_OF_WEEK_ERROR_MESSAGE, DayOfWeek.FRIDAY),
          e.getMessage());
    }
  }

}
