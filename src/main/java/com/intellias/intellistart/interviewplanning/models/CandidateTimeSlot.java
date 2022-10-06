package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * Candidate time slot.
 */
@Entity
@Data
public class CandidateTimeSlot {

  @Id
  private Long id;
  @Column(name = "from_time")
  private LocalTime from;
  @Column(name = "to_time")
  private LocalTime to;
  private LocalDate date;

  /**
   * Constructor.
   *
   * @param date date
   * @param from start time
   * @param to   end time
   */
  public CandidateTimeSlot(String date, String from, String to) {
    Period period = new Period(from, to);
    this.date = LocalDate.parse(date);
    this.from = period.getFrom();
    this.to = period.getTo();
  }

  public CandidateTimeSlot() {
  }
}
