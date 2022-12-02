package com.intellias.intellistart.interviewplanning.controllers.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * DTO from front end request.
 */
@Getter
@EqualsAndHashCode
public class BookingLimitDto {

  private final Integer bookingLimit;
  private final Integer weekNum;

  /**
   * Constructor.
   *
   * @param bookingLimit booking limit
   * @param weekNum      week number
   */
  public BookingLimitDto(Integer bookingLimit, Integer weekNum) {

    this.bookingLimit = bookingLimit;
    this.weekNum = weekNum;
  }

}
