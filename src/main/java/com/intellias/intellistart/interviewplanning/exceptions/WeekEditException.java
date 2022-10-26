package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Week edit exception.
 */
public class WeekEditException extends ApplicationErrorException {

  public WeekEditException(String errorMessage) {
    super(ErrorCode.CANNOT_EDIT_THIS_WEEK, errorMessage);
  }

  /**
   * Constructor.
   *
   * @param weekNum       week number
   * @param interviewerId interviewer's id
   */
  public WeekEditException(int weekNum, long interviewerId) {
    this("cannot edit bookings limit for user with id: "
        + interviewerId
        + " in week: "
        + weekNum);
  }
}