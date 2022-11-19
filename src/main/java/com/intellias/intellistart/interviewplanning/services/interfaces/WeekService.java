package com.intellias.intellistart.interviewplanning.services.interfaces;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Week service.
 */
public interface WeekService {

  LocalDate getCurrentDate();

  /**
   * Defines the number of the current week.
   *
   * @return number of week
   */
  int getCurrentWeekNum();

  /**
   * Defines the number of the next week.
   *
   * @return number of week
   */
  int getNextWeekNum();

  /**
   * Defines the number of the week by date.
   *
   * @param date local date
   * @return number of week
   */
  int getWeekNumByDate(LocalDate date);

  /**
   * Defines the date by number of the week and day of week.
   *
   * @param weekNum   number of week
   * @param dayOfWeek day of week
   * @return local date
   */
  LocalDate getDateByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek);

  DayOfWeek getNowDay();
}
