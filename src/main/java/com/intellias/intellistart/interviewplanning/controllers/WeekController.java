package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides current and next week json responses.
 */
@RestController
public class WeekController {

  private final CoordinatorService coordinatorService;

  /**
   * Constructor.
   *
   * @param coordinatorService coordinator service
   */
  @Autowired
  public WeekController(CoordinatorService coordinatorService) {
    this.coordinatorService = coordinatorService;
  }

  @GetMapping("/weeks/current")
  public WeekNum getCurrentWeekNum() {
    return new WeekNum(WeekService.getCurrentWeekNum());
  }

  @GetMapping("/weeks/next")
  public WeekNum getNextWeekNum() {
    return new WeekNum(WeekService.getNextWeekNum());
  }

  @GetMapping("/weeks/{weekId}/dashboard")
  public DashboardDto getWeekDashboard(@PathVariable("weekId") int weekId) {
    return coordinatorService.getWeekDashboard(weekId);
  }

  static class WeekNum {

    private final int num;

    public WeekNum(int weekNum) {
      this.num = weekNum;
    }

    @JsonGetter("weekNum")
    public int getNum() {
      return num;
    }
  }
}
