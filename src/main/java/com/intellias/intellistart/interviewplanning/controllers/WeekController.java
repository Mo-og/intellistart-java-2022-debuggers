package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.services.UtilService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides curren and next week json responses.
 */
@RestController
public class WeekController {

  @GetMapping("/weeks/current")
  public WeekNum getCurrentWeekNum() {
    return new WeekNum(UtilService.getCurrentWeekNum());
  }

  @GetMapping("/weeks/next")
  public WeekNum getNextWeekNum() {
    return new WeekNum(UtilService.getNextWeekNum());
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
