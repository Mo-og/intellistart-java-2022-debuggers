package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * Interview Time slot.
 */
@Entity
@Data

public class TimeSlot {

  @Id
  private Long id;
  private LocalTime from;
  private LocalTime to;
  private DayOfWeek dayOfWeek;
  private int weekNum;


  /**
   * Constructor.
   *
   * @param form form
   */
  public TimeSlot(TimeSlotForm form) {
    from = form.getFrom();
    to = form.getTo();
    dayOfWeek = form.getDayOfWeek();
  }

  public TimeSlot() {
  }
}