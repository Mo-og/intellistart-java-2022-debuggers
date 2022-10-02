package com.intellias.intellistart.interviewplanning.services;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

/**
 * Slot form.
 */
@Data
public class InterviewerSlotForm {

  private DayOfWeek dayOfWeek;
  private LocalTime from;
  private LocalTime to;

  /**
   * Constructor.
   *
   * @param dayOfWeek day
   * @param from      start time
   * @param to        end time
   */
  public InterviewerSlotForm(DayOfWeek dayOfWeek, LocalTime from, LocalTime to) {
    this.dayOfWeek = dayOfWeek;
    this.from = from;
    this.to = to;
  }

  /**
   * Builder.
   */
  public static class InterviewerSlotFormBuilder {

    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private LocalTime to;

    /**
     * Builder start time of slot.
     *
     * @param from start time of slot
     * @return builder
     */
    public InterviewerSlotFormBuilder from(String from) {
      String[] fromStrings = from.split(":");
      this.from = LocalTime.of(Integer.parseInt(fromStrings[0]), Integer.parseInt(fromStrings[1]));
      return this;
    }

    /**
     * Builder end time.
     *
     * @param to end time of clot
     * @return builder
     */
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

