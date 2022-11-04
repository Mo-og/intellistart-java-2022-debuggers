package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.configs.CustomOauth2User;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.springframework.security.core.Authentication;

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

  public static String getEmail(Authentication authentication) {
    return ((CustomOauth2User) authentication.getPrincipal()).getEmail();
  }
}
