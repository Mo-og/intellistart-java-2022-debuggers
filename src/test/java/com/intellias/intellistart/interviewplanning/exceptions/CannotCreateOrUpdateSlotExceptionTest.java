package com.intellias.intellistart.interviewplanning.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import org.junit.jupiter.api.Test;

class CannotCreateOrUpdateSlotExceptionTest {

  private static CannotCreateOrUpdateSlotException exception;

  @Test
  void testConstructorString() {
    exception = new CannotCreateOrUpdateSlotException("test");
    try {
      throw exception;
    } catch (CannotCreateOrUpdateSlotException e) {
      assertEquals("test", e.getMessage());
      assertEquals(ErrorCode.CANNOT_CREATE_OR_UPDATE_SLOT.code, e.getErrorCode());
    }
  }

  @Test
  void testConstructorLong() {
    exception = new CannotCreateOrUpdateSlotException(1L);
    try {
      throw exception;
    } catch (CannotCreateOrUpdateSlotException e) {
      assertEquals(ErrorCode.CANNOT_CREATE_OR_UPDATE_SLOT.code, e.getErrorCode());
      assertEquals(
          String.format(CannotCreateOrUpdateSlotException.ERROR_MESSAGE, 1L),
          e.getMessage());
    }
  }

}
