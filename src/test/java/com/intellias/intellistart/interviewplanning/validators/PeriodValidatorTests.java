package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class PeriodValidatorTests {
  @Test
  void testIsValidRight() {
    assertTrue(PeriodValidator.isValid(LocalTime.of(8,0), LocalTime.of(9,30)));
    assertTrue(PeriodValidator.isValid(LocalTime.of(8,15), LocalTime.of(9,45)));
    assertTrue(PeriodValidator.isValid(LocalTime.of(8,0), LocalTime.of(22,0)));
  }

  @Test
  void testIsValidWrong() {
    assertFalse(PeriodValidator.isValid(LocalTime.of(8,0),
        LocalTime.of(9,0)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(7,0),
        LocalTime.of(9,0)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(8,0),
        LocalTime.of(23,0)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(8,0),
        LocalTime.of(10,10)));
  }

  @Test
  void testValidateWrongPeriod() {
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8,0),
            LocalTime.of(10,10)));
  }

  @Test
  void testValidateCorrectPeriod() {
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8,0),
        LocalTime.of(22,0)));
  }

}
