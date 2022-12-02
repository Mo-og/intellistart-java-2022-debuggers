package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class PeriodValidatorTests {

  private static final WeekService weekService = new WeekServiceImp();

  @Test
  void testIsValidRight() {
    assertTrue(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(9, 30)));
    assertTrue(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(10, 0)));
    assertTrue(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(22, 0)));
  }

  @Test
  void testIsValidWrong() {
    assertFalse(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(9, 0)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(7, 0), LocalTime.of(9, 30)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(23, 0)));
    assertFalse(PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(10, 10)));
  }

  @Test
  void testValidateCorrectPeriod() {
    assertDoesNotThrow(() -> PeriodValidator.isValid(LocalTime.of(8, 0), LocalTime.of(9, 30)));
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(10, 0)));
    assertDoesNotThrow(() -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(22, 0)));
  }

  @Test
  void testValidateWrongPeriod() {
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(9, 0)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(7, 0), LocalTime.of(9, 30)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(23, 0)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(10, 10)));
  }

  @Test
  void testValidateIntersection() {
    List<InterviewerTimeSlot> slots = List.of(
        new InterviewerTimeSlot("08:00", "09:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("10:30", "12:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("12:30", "14:00", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("14:30", "17:00", "Mon", weekService.getNextWeekNum())
    );

    assertDoesNotThrow(() ->
        PeriodValidator.validateIntersection(
            LocalTime.of(17, 30), LocalTime.of(19, 0), DayOfWeek.MONDAY, slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateIntersection(
            LocalTime.of(8, 0), LocalTime.of(9, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateIntersection(
            LocalTime.of(9, 0), LocalTime.of(10, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateIntersection(
            LocalTime.of(17, 0), LocalTime.of(19, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateIntersection(
            LocalTime.of(8, 0), LocalTime.of(17, 0), DayOfWeek.MONDAY, slots));
  }

}
