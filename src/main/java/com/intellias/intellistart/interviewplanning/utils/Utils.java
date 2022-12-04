package com.intellias.intellistart.interviewplanning.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Basic util class that offers common time and date operations.
 */
public abstract class Utils {

  public static final DateTimeFormatter DAY_OF_WEEK_FORMATTER = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .appendPattern("EE")
      .toFormatter(Locale.US);
  public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private Utils() {
  }

  public static String timeAsString(TemporalAccessor time) {
    return TIME_FORMATTER.format(time);
  }

  public static int getWeekNumByDate(LocalDate date) {
    return date.get(WeekFields.ISO.weekOfWeekBasedYear())
        + date.get(IsoFields.WEEK_BASED_YEAR) * 100;
  }

  public static LocalDate getDateByWeekNumAndDayOfWeek(int weekNum, DayOfWeek dayOfWeek) {
    return LocalDate.now()
        .with(IsoFields.WEEK_BASED_YEAR, weekNum / 100)
        .with(WeekFields.ISO.weekOfWeekBasedYear(), weekNum % 100)
        .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.getValue());
  }

}
