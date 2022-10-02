package com.intellias.intellistart.interviewplanning.models;

import com.intellias.intellistart.interviewplanning.services.InterviewerSlotForm;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
public class InterviewerTimeSlot {
  @Id
  private Long id;
  private LocalTime from;
  private LocalTime to;
  private DayOfWeek dayOfWeek;

  public InterviewerTimeSlot(InterviewerSlotForm form) {
    from = form.getFrom();
    to = form.getTo();
    dayOfWeek = form.getDayOfWeek();
  }

  public InterviewerTimeSlot() {
  }
}
