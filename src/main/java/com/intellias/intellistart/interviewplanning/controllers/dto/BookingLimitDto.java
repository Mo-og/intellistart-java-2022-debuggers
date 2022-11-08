package com.intellias.intellistart.interviewplanning.controllers.dto;

import java.util.Objects;
import lombok.Getter;

/**
 * DTO from front end request.
 */
@Getter
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingLimitDto that = (BookingLimitDto) o;
    return Objects.equals(bookingLimit, that.bookingLimit) && Objects.equals(
        weekNum, that.weekNum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingLimit, weekNum);
  }
}
