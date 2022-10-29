package com.intellias.intellistart.interviewplanning.models.dto;

import java.util.Objects;
import lombok.Getter;

/**
 * DTO to front end response.
 */
@Getter
public class BookingLimitResponse {

  private final Integer limit;

  public BookingLimitResponse(Integer limit) {
    this.limit = limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingLimitResponse that = (BookingLimitResponse) o;
    return Objects.equals(limit, that.limit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(limit);
  }
}
