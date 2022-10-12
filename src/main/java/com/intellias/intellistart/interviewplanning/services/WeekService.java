package com.intellias.intellistart.interviewplanning.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;

/**
 * Provides week info.
 */
public class WeekService {

  private WeekService() {
  }

  /**
   * Returns current zoned date.
   *
   * @return LocalDate representation of zoned date
   */
  public static LocalDate getCurrentDate() {
    return ZonedDateTime.now(ZoneId.of("Europe/Kiev")).toLocalDate();
  }

  /**
   * Defines the number of the current week.
   *
   * @return number of week
   */
  public static int getCurrentWeekNum() {
    return getWeekNumByDate(getCurrentDate());
  }

  /**
   * Defines the number of the next week.
   *
   * @return number of week
   */
  public static int getNextWeekNum() {
    return getWeekNumByDate(getCurrentDate().plusWeeks(1));
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
