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
  private InterviewerTimeSlot candidateSlot;

  @ManyToOne
  private InterviewerTimeSlot interviewerSlot;

  /**
   * Constructor.
   *
   * @param from            start time
   * @param to              end time
   * @param candidateSlot   candidate slot
   * @param interviewerSlot interviewer slot
   */
  public Booking(LocalTime from, LocalTime to, InterviewerTimeSlot candidateSlot,
                 InterviewerTimeSlot interviewerSlot) {
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
   * @param candidateSlot   candidate slot
   * @param interviewerSlot interviewer slot
   */
  public Booking(InterviewerTimeSlot candidateSlot, InterviewerTimeSlot interviewerSlot) {
    this.candidateSlot = candidateSlot;
    this.interviewerSlot = interviewerSlot;
    status = BookingStatus.NEW;
  }

  public Booking() {
  }

  /**
   * Booking statuses. Default is NEW.
   */
  public enum BookingStatus {
    NEW, CHANGED, PRE_BOOKED, BOOKED, DELETED
  }
}
