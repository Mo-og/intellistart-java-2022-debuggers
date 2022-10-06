package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Data;

/**
 * Interview Time slot.
 */
@Entity
@Data
public class InterviewerTimeSlot {

  @Id
  @SequenceGenerator(name = "interv_seq", sequenceName = "interviewer_slot_seq", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interv_seq")
  @Column(nullable = false)
  private Long id;
  @Column(name = "from_time")
  private LocalTime from;
  @Column(name = "to_time")
  private LocalTime to;
  private DayOfWeek dayOfWeek;
  private int weekNum;


  /**
   * Constructor.
   *
   * @param from    start time
   * @param to      end time
   * @param day     day of week
   * @param weekNum week number
   */
  public InterviewerTimeSlot(String from, String to, String day, int weekNum) {
    Period period = new Period(from, to);
    dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
    this.from = period.getFrom();
    this.to = period.getTo();
    this.weekNum = weekNum;
  }

  public InterviewerTimeSlot() {
  }
}
