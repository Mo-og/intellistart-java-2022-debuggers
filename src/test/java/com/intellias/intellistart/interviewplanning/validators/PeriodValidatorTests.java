package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(9, 0)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(7, 0), LocalTime.of(9, 30)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(23, 0)));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validate(LocalTime.of(8, 0), LocalTime.of(10, 10)));
  }

  @Test
  void validateInterviewerSlotOverlappingTest() {
    List<InterviewerTimeSlot> slots = List.of(
        new InterviewerTimeSlot("08:00", "09:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("10:30", "12:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("12:30", "14:00", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("14:30", "17:00", "Mon", weekService.getNextWeekNum())
    );

    assertDoesNotThrow(() ->
        PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(17, 30), LocalTime.of(19, 0), DayOfWeek.MONDAY, slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(8, 0), LocalTime.of(9, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(9, 0), LocalTime.of(10, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(17, 0), LocalTime.of(19, 30), DayOfWeek.MONDAY, slots));
    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(8, 0), LocalTime.of(17, 0), DayOfWeek.MONDAY, slots));

    assertDoesNotThrow(() ->
        PeriodValidator.validateInterviewerSlotOverlapping(
            LocalTime.of(8, 0), LocalTime.of(9, 30), DayOfWeek.TUESDAY, slots));
  }

  @Test
  void validateCandidateSlotOverlappingTest() {
    List<CandidateTimeSlot> slots = List.of(
        new CandidateTimeSlot("test@mail.com", "2022-12-06", "08:00", "09:30"),
        new CandidateTimeSlot("test@mail.com", "2022-12-06", "10:30", "12:30"),
        new CandidateTimeSlot("test@mail.com", "2022-12-06", "12:30", "14:00"),
        new CandidateTimeSlot("test@mail.com", "2022-12-06", "14:30", "17:00")
    );

    assertDoesNotThrow(() ->
        PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(17, 30),
            LocalTime.of(19, 0),
            LocalDate.of(2022, 12, 6) ,
            slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(8, 0),
            LocalTime.of(9, 30),
            LocalDate.of(2022, 12, 6) ,
            slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(9, 0),
            LocalTime.of(10, 30),
            LocalDate.of(2022, 12, 6) ,
            slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(17, 0),
            LocalTime.of(19, 30),
            LocalDate.of(2022, 12, 6) ,
            slots));

    assertThrows(InvalidInputException.class,
        () -> PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            LocalDate.of(2022, 12, 6) ,
            slots));

    assertDoesNotThrow(() ->
        PeriodValidator.validateCandidateSlotOverlapping(
            LocalTime.of(8, 0),
            LocalTime.of(9, 30),
            LocalDate.of(2022, 12, 7) ,
            slots));
  }

}
