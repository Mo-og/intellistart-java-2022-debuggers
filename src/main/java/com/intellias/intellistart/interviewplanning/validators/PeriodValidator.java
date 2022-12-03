package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * PeriodValidator class. Validates 'from/to' time of candidate and interviewer slots.
 */
public class PeriodValidator {

  private static final LocalTime lowerTimeExclusive = LocalTime.of(7, 59);
  private static final LocalTime upperTimeExclusive = LocalTime.of(22, 1);
  private static final Integer roundToMinutes = 30;
  private static final Integer minPeriodInMinutes = 90;

  /**
   * Method to validate period. from/to time should be rounded to 00 or 30 minutes. from time should
   * be after 7:59 AM. to time should be before 22:01 PM. minimum period should be 1.5 hours.
   *
   * @param from start time
   * @param to   end time
   * @throws InvalidInputException if period is invalid
   */
  public static void validate(LocalTime from, LocalTime to) {
    if (to.getMinute() % roundToMinutes != 0
        || from.getMinute() % roundToMinutes != 0) {
      throw InvalidInputException.minutes();
    } else if (!from.isAfter(lowerTimeExclusive)) {
      throw InvalidInputException.timeLowerBound();
    } else if (!to.isBefore(upperTimeExclusive)) {
      throw InvalidInputException.timeUpperBound();
    } else if (Duration.between(from, to).toMinutes() < minPeriodInMinutes) {
      throw InvalidInputException.period();
    }
  }

  /**
   * Method to validate interviewer time slot overlapping.
   *
   * @param from     start time
   * @param to       end time
   * @param allSlots list of slots
   */
  public static void validateInterviewerSlotOverlapping(LocalTime from, LocalTime to,
      DayOfWeek dayOfWeek,
      List<InterviewerTimeSlot> allSlots) {
    allSlots.stream()
        .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
        .forEach((slot) -> {
          if (!(slot.getFrom().isAfter(to) || slot.getTo().isBefore(from))) {
            throw InvalidInputException.periodOverlapping();
          }
        });
  }

  /**
   * Method to validate candidate time slot overlapping.
   *
   * @param from     start time
   * @param to       end time
   * @param allSlots list of slots
   */
  public static void validateCandidateSlotOverlapping(LocalTime from, LocalTime to, LocalDate date,
      List<CandidateTimeSlot> allSlots) {
    allSlots.stream()
        .filter(slot -> slot.getDate().equals(date))
        .forEach((slot) -> {
          if (!(slot.getFrom().isAfter(to) || slot.getTo().isBefore(from))) {
            throw InvalidInputException.periodOverlapping();
          }
        });
  }

  private PeriodValidator() {
  }

}
