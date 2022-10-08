package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService.Dashboard;
import com.intellias.intellistart.interviewplanning.services.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides current and next week json responses.
 */
@RestController
public class WeekController {

  @Autowired
  private CoordinatorService coordinatorService;

  @GetMapping("/weeks/current")
  public WeekNum getCurrentWeekNum() {
    return new WeekNum(UtilService.getCurrentWeekNum());
  }

  @GetMapping("/weeks/next")
  public WeekNum getNextWeekNum() {
    return new WeekNum(UtilService.getNextWeekNum());
  }

  @GetMapping("/weeks/{weekId}/dashboard")
  public Dashboard getDashboard(@PathVariable("weekId") int weekId) {
    return coordinatorService.getWeekDashboard(weekId);
  }

  private static class WeekNum {

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
