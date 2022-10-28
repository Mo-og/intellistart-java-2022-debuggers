package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Booking limit for interviewer per week.
 */
@Getter
@Setter
@Entity
public class BookingLimit {

  @Id
  @GeneratedValue
  private Long id;
  private Long interviewerId;
  private int weekNum;
  private int bookingLimit;

  /**
   * Constructor.
   *
   * @param weekNum          week number
   * @param limit            limit of bookings
   */
  public BookingLimit(Long interviewerId, int weekNum, int limit) {
    this.interviewerId = interviewerId;
    this.weekNum = weekNum;
    this.bookingLimit = limit;
  }

  public BookingLimit() {

  }
}