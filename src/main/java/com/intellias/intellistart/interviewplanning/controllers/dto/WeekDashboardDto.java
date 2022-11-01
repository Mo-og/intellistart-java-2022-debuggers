package com.intellias.intellistart.interviewplanning.controllers.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Week dashboard dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekDashboardDto {

  private Set<DayDashboardDto> days;
}
