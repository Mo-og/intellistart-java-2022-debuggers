package com.intellias.intellistart.interviewplanning.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Provides week info.
 */
@Service
@Qualifier("WeekService")
public class WeekServiceImp implements WeekService {

  /**
   * Returns current zoned date.
   *
   * @return LocalDate representation of zoned date
   */
  public LocalDate getCurrentDate() {
    return ZonedDateTime.now(ZoneId.of("Europe/Kiev")).toLocalDate();
  }

  /**
   * Defines the number of the current week.
   *
   * @return number of week
   */
  public int getCurrentWeekNum() {
    return getWeekNumByDate(getCurrentDate());
  }

  /**
   * Defines the number of the next week.
   *
   * @return number of week
   */
  public int getNextWeekNum() {
    return getWeekNumByDate(getCurrentDate().plusWeeks(1));
  }

  /**
   * Defines the number of the week by date.
   *
   * @param date local date
   * @return number of week
   */
  public int getWeekNumByDate(LocalDate date) {
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
  public LocalDate getDateByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek) {
    return LocalDate.now()
        .with(IsoFields.WEEK_BASED_YEAR, weekNum / 100)
        .with(WeekFields.ISO.weekOfWeekBasedYear(), weekNum % 100)
        .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.getValue());
  }
}
