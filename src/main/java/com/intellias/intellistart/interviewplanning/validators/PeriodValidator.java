package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.BadRequestException;
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
        && Math.abs(toTime.getMinute() - fromTime.getMinute()) % 30 == 0
        && fromTime.isAfter(LocalTime.of(7, 59))
        && toTime.isBefore(LocalTime.of(22, 1));
  }

  /**
   * Method to validate period.
   *
   * @param from start time
   * @param to   end time
   * @throws BadRequestException if period is invalid
   */
  public static void validate(String from, String to) {
    if (!isValid(from, to)) {
      throw BadRequestException.invalidBoundaries(from, to);
    }
  }

  private PeriodValidator() {
  }

}
