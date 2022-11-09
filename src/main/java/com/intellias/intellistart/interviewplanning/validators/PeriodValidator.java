package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import java.time.LocalTime;

/**
 * PeriodValidator class.
 */
public class PeriodValidator {

  /**
   * Checks if period is valid.
   *
   * @param from start time
   * @param to   end time
   * @return true if period is valid
   */
  public static boolean isValid(LocalTime from, LocalTime to) {
    return to.minusMinutes(89).isAfter(from)
        && Math.abs(to.getMinute() - from.getMinute()) % 30 == 0
        && from.isAfter(LocalTime.of(7, 59))
        && to.isBefore(LocalTime.of(22, 1));
  }

  /**
   * Method to validate period.
   *
   * @param from start time
   * @param to   end time
   * @throws InvalidInputException if period is invalid
   */
  public static void validate(LocalTime from, LocalTime to) {
    if (!isValid(from, to)) {
      throw InvalidInputException.boundaries(from.toString(), to.toString());
    }
  }

  private PeriodValidator() {
  }

}
