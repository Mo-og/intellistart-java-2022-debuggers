package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 * Booking.
 *
 */
@Entity
@Data
public class Booking {

  @Id
  private Long id;
  private LocalTime from;
  private LocalTime to;
  private BookingStatus status;
  @ManyToOne
  private TimeSlot candidateSlot;

  @ManyToOne
  private TimeSlot interviewerSlot;

  /**
   * Constructor.
   *
   * @param from start time
   * @param to end time
   * @param candidateSlot candidate slot
   * @param interviewerSlot interviewer slot
   */
  public Booking(LocalTime from, LocalTime to, TimeSlot candidateSlot,
      TimeSlot interviewerSlot) {
    //TODO: better way to create booking
    this.from = from;
    this.to = to;
    this.candidateSlot = candidateSlot;
    this.interviewerSlot = interviewerSlot;
    status = BookingStatus.NEW;
  }

  /**
   * Constructor.
   *
   * @param candidateSlot candidate slot
   * @param interviewerSlot interviewer slot
   */
  public Booking(TimeSlot candidateSlot, TimeSlot interviewerSlot) {
    this.candidateSlot = candidateSlot;
    this.interviewerSlot = interviewerSlot;
    status = BookingStatus.NEW;
  }

  public Booking() {
  }
}
