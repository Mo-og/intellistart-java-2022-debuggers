package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for getting week information.
 */
@RestController
@RequiredArgsConstructor
public class WeekController {

  private final CoordinatorService coordinatorService;
  private final WeekService weekService;

  /**
   * Endpoint to get the number of current week.
   *
   * @return current week number
   */
  @GetMapping("/weeks/current")
  public WeekNum getCurrentWeekNum() {
    return new WeekNum(weekService.getCurrentWeekNum());
  }

  /**
   * Endpoint to get the number of next week.
   *
   * @return next week number
   */
  @GetMapping("/weeks/next")
  public WeekNum getNextWeekNum() {
    return new WeekNum(weekService.getNextWeekNum());
  }

  /**
   * Endpoint to get week dashboard.
   *
   * @return dashboard for the week
   */
  @GetMapping("/weeks/{weekId}/dashboard")
  public DashboardDto getWeekDashboard(@PathVariable("weekId") int weekId) {
    return coordinatorService.getWeekDashboard(weekId);
  }

  /**
   * Simple week number DTO.
   */
  @RequiredArgsConstructor
  static class WeekNum {

    private final int num;

    @JsonGetter("weekNum")
    public int getNum() {
      return num;
    }
  }
}
