package com.intellias.intellistart.interviewplanning.services;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Data
public class InterviewerSlotForm {
  private DayOfWeek dayOfWeek;
  private LocalTime from;
  private LocalTime to;

  public InterviewerSlotForm(DayOfWeek dayOfWeek, LocalTime from, LocalTime to) {
    this.dayOfWeek = dayOfWeek;
    this.from = from;
    this.to = to;
  }


  public static class InterviewerSlotFormBuilder {
    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private LocalTime to;

    public InterviewerSlotFormBuilder from(String from) {
      String[] fromStrings = from.split(":");
      this.from = LocalTime.of(Integer.parseInt(fromStrings[0]), Integer.parseInt(fromStrings[1]));
      return this;
    }

    public InterviewerSlotFormBuilder to(String to) {
      String[] toStrings = to.split(":");
      this.to = LocalTime.of(Integer.parseInt(toStrings[0]), Integer.parseInt(toStrings[1]));
      return this;
    }

    public InterviewerSlotFormBuilder dayOfWeek(String day) {
      this.dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
      return this;
    }

    public InterviewerSlotForm build() {
      return new InterviewerSlotForm(dayOfWeek, from, to);
    }

  }

  public static InterviewerSlotFormBuilder builder() {
    return new InterviewerSlotFormBuilder();
  }

}

