package com.intellias.intellistart.interviewplanning.models.interfaces;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.LocalTime;

/**
 * TimeSlot interface. Represents time slot.
 */
public interface TimeSlot {

  LocalTime getFrom();

  LocalTime getTo();

  @JsonGetter("from")
  default String getFromAsString() {
    return Utils.timeAsString(getFrom());
  }

  @JsonGetter("to")
  default String getToAsString() {
    return Utils.timeAsString(getTo());
  }

  default boolean isAfterOrEqual(TimeSlot slot) {
    return getFrom().isAfter(slot.getTo()) || getFrom().equals(slot.getTo());
  }

  default boolean isBeforeOrEqual(TimeSlot slot) {
    return getTo().isBefore(slot.getFrom()) || getTo().equals(slot.getFrom());
  }

}
