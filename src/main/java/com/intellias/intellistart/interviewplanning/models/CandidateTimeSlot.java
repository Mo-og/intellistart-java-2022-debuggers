package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Data;

/**
 * Candidate time slot.
 */
@Entity
@Data
public class CandidateTimeSlot {

  @Id
  @SequenceGenerator(name = "cnd_seq", sequenceName = "candidate_slot_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnd_seq")
  @Column(nullable = false)
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
