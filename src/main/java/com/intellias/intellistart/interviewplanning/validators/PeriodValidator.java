package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidPeriodException;
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
  public static boolean isValid(String from, String to) {
    LocalTime fromTime = LocalTime.parse(from);
    LocalTime toTime = LocalTime.parse(to);

    return toTime.minusMinutes(89).isAfter(fromTime)
        || (toTime.getMinute() - fromTime.getMinute()) % 30 != 0
        || fromTime.isBefore(LocalTime.of(8, 0))
        || toTime.isAfter(LocalTime.of(22, 0));
  }

  /**
   * Method to validate period.
   *
   * @param from start time
   * @param to   end time
   * @throws InvalidPeriodException if period is invalid
   */
  public static void validate(String from, String to) throws InvalidPeriodException {
    if (!isValid(from, to)) {
      throw new InvalidPeriodException(from, to);
    }
  }

  private PeriodValidator() {
  }

}
