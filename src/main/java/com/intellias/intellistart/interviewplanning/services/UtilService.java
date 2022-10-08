package com.intellias.intellistart.interviewplanning.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

/**
 * Provides week info.
 */
public class UtilService {

  private UtilService() {
  }

  public static int getCurrentWeekNum() {
    var date = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
    return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + date.get(ChronoField.YEAR) * 100;
  }

  public static int getNextWeekNum() {
    return getCurrentWeekNum() + 1;
  }

  public static int getWeekNumByDate(LocalDate date) {
    return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + date.get(ChronoField.YEAR) * 100;
  }
}
