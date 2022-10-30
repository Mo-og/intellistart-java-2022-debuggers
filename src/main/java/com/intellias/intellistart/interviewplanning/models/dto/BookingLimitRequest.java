package com.intellias.intellistart.interviewplanning.models.dto;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingLimitRequest that = (BookingLimitRequest) o;
    return Objects.equals(bookingLimit, that.bookingLimit) && Objects.equals(
        weekNum, that.weekNum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingLimit, weekNum);
  }
}
