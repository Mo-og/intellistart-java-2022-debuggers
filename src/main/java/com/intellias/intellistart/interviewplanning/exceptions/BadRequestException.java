package com.intellias.intellistart.interviewplanning.exceptions;

import java.time.DayOfWeek;

/**
 * BadRequestException class.
 */
public class BadRequestException extends ApplicationErrorException {

  /**
   * constructor.
   *
   * @param errorMessage error message
   */
  public BadRequestException(ErrorCode errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public static BadRequestException invalidBoundaries(String from, String to) {
    return new BadRequestException(ErrorCode.INVALID_BOUNDARIES,
        String.format(": %s - %s", from, to));
  }

  public static BadRequestException invalidDayOfWeek(DayOfWeek dayOfWeek) {
    return new BadRequestException(ErrorCode.INVALID_DAY_OF_WEEK,
        String.format(": cannot create or edit slot on %s", dayOfWeek.toString().toLowerCase()));
  }

  public static BadRequestException invalidWeekNum(int weekNum) {
    return new BadRequestException(ErrorCode.INVALID_WEEK_NUM,
        String.format(": cannot create or edit booking limit on week %d", weekNum));
  }

  /**
   * Invalid booking limit exception.
   *
   * @param bookingLimit limit of bookings
   * @param bookingNum   number of existing bookings
   * @return exception
   */
  public static BadRequestException invalidBookingLimit(int bookingLimit, int bookingNum) {
    return new BadRequestException(ErrorCode.INVALID_BOOKING_LIMIT,
        String.format(
            ": booking limit \"%d\" cannot be lower than the number of existing bookings \"%d\"",
            bookingLimit, bookingNum));
  }


}
