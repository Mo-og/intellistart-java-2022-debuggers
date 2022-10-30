package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * BookingLimit exception.
 */
public class BookingLimitException extends ApplicationErrorException {

  public BookingLimitException(String errorMessage) {
    super(ErrorCode.INVALID_BOOKING_LIMIT, errorMessage);
  }

  /**
   * Constructor.
   *
   * @param bookingLimit limit of bookings
   * @param bookingNum   number of existing booking
   */
  public BookingLimitException(int bookingLimit, int bookingNum) {
    this("The booking limit cannot ("
        + bookingLimit
        + ") be lower than the number of existing bookings ("
        + bookingNum + ")");
  }
}
