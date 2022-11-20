package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InterviewerSlot dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerSlotDto {

  private Long id;
  private int weekNum;
  private String dayOfWeek;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
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
}
