package com.intellias.intellistart.interviewplanning.exceptions;

import java.time.DayOfWeek;

/**
 * InvalidInputException class.
 */
public class InvalidInputException extends TemplateMessageException {

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public InvalidInputException(ErrorCode errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  /**
   * Invalid time upper bound exception.
   *
   * @return exception
   */
  public static InvalidInputException timeUpperBound() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": end time cannot be after 10PM");
  }

  /**
   * Invalid time lower bound exception.
   *
   * @return exception
   */
  public static InvalidInputException timeLowerBound() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": start time cannot be before 8AM");
  }

  /**
   * Invalid time rounding exception.
   *
   * @param time time
   * @return exception
   */
  public static InvalidInputException rounding(String time) {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        String.format(": '%s' should be rounded to 00 or 30 minutes", time));
  }

  /**
   * Invalid min period exception.
   *
   * @return exception
   */
  public static InvalidInputException minPeriod() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": period cannot be less than 1.5h");
  }

  /**
   * Period intersection exception.
   *
   * @return exception
   */
  public static InvalidInputException periodIntersection() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": slot at this time already exists");
  }

  /**
   * Invalid day of week exception.
   *
   * @param dayOfWeek day of week
   * @return exception
   */
  public static InvalidInputException dayOfWeek(DayOfWeek dayOfWeek) {
    return new InvalidInputException(ErrorCode.INVALID_DAY_OF_WEEK,
        String.format(": cannot create or edit slot on %s", dayOfWeek.toString().toLowerCase()));
  }

  /**
   * Invalid week number exception.
   *
   * @param weekNum week number
   * @return exception
   */
  public static InvalidInputException weekNum(int weekNum) {
    return new InvalidInputException(ErrorCode.INVALID_WEEK_NUM,
        String.format(": cannot create or edit booking limit on week '%d'", weekNum));
  }

  /**
   * Invalid slot week number exception.
   *
   * @param weekNum week number
   * @return exception
   */
  public static InvalidInputException slotWeekNum(int weekNum) {
    return new InvalidInputException(ErrorCode.CANNOT_EDIT_THIS_WEEK,
        String.format(": cannot create or edit slot on week '%d'", weekNum));
  }

  public static InvalidInputException exceedsBookingLimit(int bookingLimit) {
    return new InvalidInputException(ErrorCode.CANNOT_CREATE_BOOKING,
        String.format(": exceeds interviewer booking limit %d", bookingLimit));
  }

  /**
   * Invalid booking limit exception.
   *
   * @param bookingLimit limit of bookings
   * @param bookingNum   number of existing bookings
   * @return exception
   */
  public static InvalidInputException bookingLimit(int bookingLimit, int bookingNum) {
    return new InvalidInputException(ErrorCode.INVALID_BOOKING_LIMIT,
        String.format(
            ": booking limit '%d' cannot be lower than the number of existing bookings '%d'",
            bookingLimit, bookingNum));
  }

}
