package com.intellias.intellistart.interviewplanning.exceptions;

import java.time.DayOfWeek;

/**
 * InvalidPeriodException class.
 */
public class InvalidBoundariesException extends ApplicationErrorException {

  public static final String PERIOD_ERROR_MESSAGE = "Invalid period: %s - %s";
  public static final String DAY_OF_WEEK_ERROR_MESSAGE = "Invalid day of week: %s";

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public InvalidBoundariesException(String errorMessage) {
    super(ErrorCode.INVALID_BOUNDARIES, errorMessage);
  }

  /**
   * constructor.
   *
   * @param from start time
   * @param to   end time
   */
  public InvalidBoundariesException(String from, String to) {
    this(String.format(PERIOD_ERROR_MESSAGE, from, to));
  }

  public InvalidBoundariesException(DayOfWeek dayOfWeek) {
    super(ErrorCode.INVALID_DAY_OF_WEEK,
        String.format(DAY_OF_WEEK_ERROR_MESSAGE, dayOfWeek.toString()));
  }

}
