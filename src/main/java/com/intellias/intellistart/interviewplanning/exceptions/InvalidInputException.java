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

  public static InvalidInputException boundaries(String from, String to) {
    return new InvalidInputException(ErrorCode.INVALID_BOUNDARIES,
        String.format(": %s - %s", from, to));
  }

  public static InvalidInputException dayOfWeek(DayOfWeek dayOfWeek) {
    return new InvalidInputException(ErrorCode.INVALID_DAY_OF_WEEK,
        String.format(": cannot create or edit slot on %s", dayOfWeek.toString().toLowerCase()));
  }

  public static InvalidInputException slotWeekNum(int weekNum) {
    return new InvalidInputException(ErrorCode.INVALID_WEEK_NUM,
        String.format(": cannot create or edit slot on week %d", weekNum));
  }

  public static InvalidInputException weekNum(int weekNum) {
    return new InvalidInputException(ErrorCode.INVALID_WEEK_NUM,
        String.format(": cannot create or edit booking limit on week %d", weekNum));
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
            ": booking limit \"%d\" cannot be lower than the number of existing bookings \"%d\"",
            bookingLimit, bookingNum));
  }


}
