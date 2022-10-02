package com.intellias.intellistart.interviewplanning.models;

import com.intellias.intellistart.interviewplanning.services.InterviewerSlotForm;
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

public class InterviewerTimeSlot {

  @Id
  private Long id;
  private LocalTime from;
  private LocalTime to;
  private DayOfWeek dayOfWeek;

  /**
   * Constructor.
   *
   * @param form form
   */
  public InterviewerTimeSlot(InterviewerSlotForm form) {
    from = form.getFrom();
    to = form.getTo();
    dayOfWeek = form.getDayOfWeek();
  }

  public InterviewerTimeSlot() {
  }
}
