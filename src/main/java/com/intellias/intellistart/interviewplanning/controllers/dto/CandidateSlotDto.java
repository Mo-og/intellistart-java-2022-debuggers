package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.models.interfaces.TimeSlot;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Candidate slot dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSlotDto implements TimeSlot {

  private Long id;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
  private LocalDate date;
  @JsonInclude(Include.NON_EMPTY)
  private List<BookingDto> bookings;

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }

  @JsonGetter("date")
  public String getDateAsString() {
    return date.toString();
  }

  @Override
  public DayOfWeek getDayOfWeek() {
    return date.getDayOfWeek();
  }

  @Override
  public int getWeekNum() {
    return Utils.getWeekNumByDate(date);
  }

}
