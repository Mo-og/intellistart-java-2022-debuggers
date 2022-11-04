package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.CannotCreateOrUpdateSlotException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidBoundariesException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * InterviewerSlotValidate class. Used to validate interviewer time slot before creating or
 * updating.
 */
public class InterviewerSlotValidator {

  private static final String WEEKEND_MESSAGE = "Cannot %s slot on weekend";
  private static final String NEXT_WEEK_MESSAGE = "Can %s slot only for next week";

  private InterviewerSlotValidator() {
  }

  /**
   * Validate interviewer time slot to be created.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @param action              CREATE or UPDATE
   * @throws CannotCreateOrUpdateSlotException if interviewer time slot is invalid
   * @throws InvalidBoundariesException         if editing slot on weekend
   */
  public static void validate(InterviewerTimeSlot interviewerTimeSlot, Action action) {
    var now = LocalDate.now();
    if (isWeekend(now.getDayOfWeek())) {
      throw new InvalidBoundariesException(
          String.format(WEEKEND_MESSAGE, action));
    } else if (interviewerTimeSlot.getWeekNum() != WeekService.getNextWeekNum()) {
      throw new CannotCreateOrUpdateSlotException(
          String.format(NEXT_WEEK_MESSAGE, action));
    }
  }

  private static boolean isWeekend(DayOfWeek dayOfWeek) {
    return dayOfWeek.getValue() > DayOfWeek.FRIDAY.getValue();
  }

  /**
   * Action enum.
   */
  public enum Action {
    CREATE, UPDATE
  }

}
