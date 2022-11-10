package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

class PeriodValidatorTests {

  @Test
  void testIsValidRight() {
    assertTrue(PeriodValidator.isValid("08:00", "09:30"));
    assertTrue(PeriodValidator.isValid("08:15", "09:45"));
    assertTrue(PeriodValidator.isValid("08:00", "22:00"));
  }

  @Test
  void testIsValidWrong() {
    assertFalse(PeriodValidator.isValid("08:00", "09:00"));
    assertFalse(PeriodValidator.isValid("07:00", "09:00"));
    assertFalse(PeriodValidator.isValid("08:00", "23:00"));
    assertFalse(PeriodValidator.isValid("08:00", "10:10"));
  }

  @Test
  void testValidateWrongPeriod() {
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate("08:00", "10:10"));
  }

  @Test
  void testValidateCorrectPeriod() {
    assertDoesNotThrow(() -> PeriodValidator.validate("08:00", "22:00"));
  }

}
