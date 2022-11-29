package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import java.time.Duration;
import java.time.LocalTime;

/**
 * PeriodValidator class.
 */
public class PeriodValidator {

  private static final LocalTime lowerTimeExclusive = LocalTime.of(7, 59);
  private static final LocalTime upperTimeExclusive = LocalTime.of(22, 1);
  private static final Integer roundToMinutes = 30;
  private static final Integer minPeriodInMinutes = 90;
  /**
   * Checks if period is valid.
   *
   * @param from start time
   * @param to   end time
   * @return true if period is valid
   */
  public static boolean isValid(LocalTime from, LocalTime to) {
    return to.minusMinutes(89).isAfter(from)
        && to.getMinute() % roundToMinutes == 0
        && from.getMinute() % roundToMinutes == 0
        && from.isAfter(lowerTimeExclusive)
        && to.isBefore(upperTimeExclusive)
        && Duration.between(to, from).toMinutes() >= minPeriodInMinutes;
  }

  /**
   * Method to validate period.
   *
   * @param from start time
   * @param to   end time
   * @throws InvalidInputException if period is invalid
   */
  public static void validate(LocalTime from, LocalTime to) {
    if (to.getMinute() % roundToMinutes != 0) {
      throw InvalidInputException.notRounded(to.toString());
    } else if (from.getMinute() % roundToMinutes != 0) {
      throw InvalidInputException.notRounded(from.toString());
    } else if (!from.isAfter(lowerTimeExclusive)) {
      throw InvalidInputException.timeLowerBound();
    } else if (!to.isBefore(upperTimeExclusive)) {
      throw InvalidInputException.timeUpperBound();
    } else if (Duration.between(to, from).toMinutes() < minPeriodInMinutes) {
      throw InvalidInputException.minPeriod();
    }
  }

  private PeriodValidator() {
  }

}
