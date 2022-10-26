package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
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
  private String email;
  private int weekNum;
  private int bookingLimit;

  /**
   * Constructor.
   *
   * @param email   interviewer's email
   * @param weekNum week number
   * @param limit   limit of bookings
   */
  public BookingLimit(String email, int weekNum, int limit) {
    this.email = email;
    this.weekNum = weekNum;
    this.bookingLimit = limit;
  }

  public BookingLimit() {

  }
}