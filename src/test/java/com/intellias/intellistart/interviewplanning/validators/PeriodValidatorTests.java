package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import org.junit.jupiter.api.Test;

class PeriodValidatorTests {

  private static final WeekService weekService = new WeekServiceImp();

  @Test
  void validateCorrectPeriodTest() {
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(9, 30)));
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(10, 0)));
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(22, 0)));
  }

  @Test
  void validateWrongPeriodTest() {
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("08:00", "09:00", "Mon", 1)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("07:00", "09:36", "Mon", 1)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("08:00", "23:00", "Mon", 1)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("08:00", "10:10", "Mon", 1)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("07:00", "23:00", "Mon", 1)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(
            new InterviewerTimeSlot("07:00", "10:00", "Mon", 1)));
  }

}
