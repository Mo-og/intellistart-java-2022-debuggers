package com.intellias.intellistart.interviewplanning.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;

/**
 * Provides week info.
 */
public class WeekService {

  public static final ZoneId ZONE_ID = ZoneId.of("Europe/Kiev");

  private WeekService() {
  }

  /**
   * Returns current zoned date.
   *
   * @return LocalDate representation of zoned date
   */
  public static LocalDate getCurrentDate() {
    return ZonedDateTime.now(ZONE_ID).toLocalDate();
  }

  /**
   * Returns current zoned date and time.
   *
   * @return LocalDateTime representation of zoned date and time
   */
  public static LocalDateTime getCurrentDateTime() {
    return ZonedDateTime.now(ZONE_ID).toLocalDateTime();
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

  /**
   * Defines the date by number of the week and day of week.
   *
   * @param weekNum   number of week
   * @param dayOfWeek day of week
   * @return local date
   */
  public static LocalDate getDateByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek) {
    return LocalDate.now()
        .with(IsoFields.WEEK_BASED_YEAR, weekNum / 100)
        .with(WeekFields.ISO.weekOfWeekBasedYear(), weekNum % 100)
        .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.getValue());
  }
}
