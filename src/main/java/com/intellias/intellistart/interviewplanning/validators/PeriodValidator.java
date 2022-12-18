package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.interfaces.Period;
import java.time.Duration;
import java.time.LocalTime;

/**
 * PeriodValidator class. Validates "from/to" time of candidate and interviewer slots.
 */
public class PeriodValidator {

  private static final LocalTime WORK_START_TIME_EXCLUSIVE = LocalTime.of(7, 59);
  private static final LocalTime WORK_END_TIME_EXCLUSIVE = LocalTime.of(22, 1);
  private static final Integer TIME_STEP = 30;
  private static final Integer MIN_PERIOD_IN_MINUTES = 90;

  /**
   * Method to validate period. "from/to" time should be rounded to 00 or 30 minutes. "from" time should be after 7:59.
   * "to" time should be before 22:01. Minimum period should be 1.5 hours.
   *
   * @throws InvalidInputException if period is invalid
   */
  public static void validate(Period period) {
    if (period.getTo().getMinute() % TIME_STEP != 0 || period.getFrom().getMinute() % TIME_STEP != 0) {
      throw InvalidInputException.minutes();
    } else if (period.getFrom().isBefore(WORK_START_TIME_EXCLUSIVE)) {
      throw InvalidInputException.timeLowerBound();
    } else if (period.getTo().isAfter(WORK_END_TIME_EXCLUSIVE)) {
      throw InvalidInputException.timeUpperBound();
    } else if (Duration.between(period.getFrom(), period.getTo()).toMinutes() < MIN_PERIOD_IN_MINUTES) {
      throw InvalidInputException.period();
    }
  }

  private PeriodValidator() {
  }

}
