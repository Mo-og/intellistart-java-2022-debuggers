package com.intellias.intellistart.interviewplanning.models.dto;

import lombok.Getter;

/**
 * DTO from front end request.
 */
@Getter
public class BookingLimitRequest {

  private final Integer bookingLimit;
  private final Integer weekNum;

  /**
   * Constructor.
   *
   * @param bookingLimit booking limit
   * @param weekNum      week number
   */
  public BookingLimitRequest(Integer bookingLimit, Integer weekNum) {

    this.bookingLimit = bookingLimit;
    this.weekNum = weekNum;
  }
}
