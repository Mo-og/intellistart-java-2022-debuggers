package com.intellias.intellistart.interviewplanning.controllers.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Day dashboard dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayDashboardDto {

  private LocalDate date;
  private String dayOfWeek;
  private List<InterviewerSlotDto> interviewerSlots;
  private List<CandidateSlotDto> candidateSlots;
  private Map<Long, BookingDto> bookings;
}
