package com.intellias.intellistart.interviewplanning.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;

/**
 * Provides week info.
 */
public class UtilService {

  private UtilService() {
  }

  /**
   * Defines the number of the current week.
   *
   * @return number of week
   */
  public static int getCurrentWeekNum() {
    // Todo Define the current week according to ISO 8601
    var date = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
    return date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + date.get(ChronoField.YEAR) * 100;
  }

  /**
   * Defines the number of the next week.
   *
   * @return number of week
   */
  public static int getNextWeekNum() {
    // Todo Define the next week according to ISO 8601
    return getCurrentWeekNum() + 1;
  }

  /**
   * Defines the number of the week by date.
   *
   * @param date local date
   * @return number of week
   */
  public static int getWeekNumByDate(LocalDate date) {
    return date.get(WeekFields.ISO.weekOfWeekBasedYear())
        + date.get(IsoFields.WEEK_BASED_YEAR) * 100;
  }
}
