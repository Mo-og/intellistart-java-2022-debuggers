package com.intellias.intellistart.interviewplanning.utils;

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

  /**
   * Get time as string from temporal accessor.
   *
   * @param time time
   * @return time as string
   */
  public static String timeAsString(TemporalAccessor time) {
    return TIME_FORMATTER.format(time);
  }

  /**
   * Get week num from temporal accessor.
   *
   * @param date date
   * @return day of week
   */
  public static int getWeekNumByDate(TemporalAccessor date) {
    return date.get(WeekFields.ISO.weekOfWeekBasedYear())
        + date.get(IsoFields.WEEK_BASED_YEAR) * 100;
  }

}
