package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.models.interfaces.Booking;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto implements Booking {

  private Long id;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
  private String subject;
  private String description;
  private Long interviewerSlotId;
  private Long candidateSlotId;

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }
}
