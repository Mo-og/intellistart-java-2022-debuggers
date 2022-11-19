package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
}
