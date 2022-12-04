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
        ": end time cannot be after 22:00");
  }

  /**
   * Invalid time lower bound exception.
   *
   * @return exception
   */
  public static InvalidInputException timeLowerBound() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": start time cannot be before 08:00");
  }

  /**
   * Invalid time rounding exception.
   *
   * @return exception
   */
  public static InvalidInputException minutes() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": minutes should be rounded to 00 or 30");
  }

  /**
   * Invalid min period exception.
   *
   * @return exception
   */
  public static InvalidInputException period() {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        ": period cannot be less than 1.5h");
  }

  /**
   * Period intersection exception.
   *
   * @return exception
   */
  public static InvalidInputException periodOverlapping() {
    return new InvalidInputException(ErrorCode.SLOT_IS_OVERLAPPING,
        ": slot at provided time already exists");
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

  /**
   * Exceeds booking limit exception.
   *
   * @param bookingLimit booking limit
   * @return exception
   */
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

  /**
   * Invalid sate time exception
   *
   * @return exception
   */
  public static InvalidInputException dateTime() {
    return new InvalidInputException(ErrorCode.INVALID_DATE_TIME,
        ": slot cannot be before current date and time");
  }

}
