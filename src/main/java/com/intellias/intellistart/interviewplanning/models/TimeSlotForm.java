package com.intellias.intellistart.interviewplanning.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;

/**
 * Slot form.
 */
@Data
public class TimeSlotForm {

  private DayOfWeek dayOfWeek;
  private LocalTime from;
  private LocalTime to;
  private int weekNum;

  /**
   * Constructor.
   *
   * @param dayOfWeek day
   * @param from      start time
   * @param to        end time
   */
  public TimeSlotForm(DayOfWeek dayOfWeek, LocalTime from, LocalTime to, int weekNum) {
    this.dayOfWeek = dayOfWeek;
    this.from = from;
    this.to = to;
    this.weekNum = weekNum;
  }

  /**
   * Builder.
   */
  public static class TimeSlotFormBuilder {

    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private LocalTime to;
    private int weekNum;

    /**
     * Builder start time of slot.
     *
     * @param from start time of slot
     * @return builder
     */
    public TimeSlotFormBuilder from(String from) {
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
    public TimeSlotFormBuilder to(String to) {
      String[] toStrings = to.split(":");
      this.to = LocalTime.of(Integer.parseInt(toStrings[0]), Integer.parseInt(toStrings[1]));
      return this;
    }

    public TimeSlotFormBuilder dayOfWeek(String day) {
      this.dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
      return this;
    }

    public TimeSlotFormBuilder weekNum(int weekNum) {
      this.weekNum = weekNum;
      return this;
    }

    public TimeSlotForm build() {
      return new TimeSlotForm(dayOfWeek, from, to, weekNum);
    }

  }

  public static TimeSlotFormBuilder builder() {
    return new TimeSlotFormBuilder();
  }

}

