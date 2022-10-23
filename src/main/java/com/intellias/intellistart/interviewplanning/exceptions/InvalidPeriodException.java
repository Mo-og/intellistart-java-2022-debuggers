package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * InvalidPeriodException class.
 */
public class InvalidPeriodException extends ApplicationErrorException {

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public InvalidPeriodException(String errorMessage) {
    super(ErrorCode.INVALID_PERIOD, errorMessage);
  }

  /**
   * constructor.
   *
   * @param from start time
   * @param to   end time
   */
  public InvalidPeriodException(String from, String to) {
    this("Invalid period: " + from + " - " + to);
  }
}
