package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.BadRequestException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * InterviewerSlotValidate class. Used to validate interviewer time slot before creating or
 * updating.
 */
public class InterviewerSlotValidator {

  private InterviewerSlotValidator() {
  }

  /**
   * Validate interviewer time slot to be created.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @throws BadRequestException if editing slot no to yhe next week
   * @throws BadRequestException if editing slot on weekend
   */
  public static void validate(InterviewerTimeSlot interviewerTimeSlot) {
    var now = LocalDate.now().getDayOfWeek();
    if (isWeekend(now)) {
      throw BadRequestException.invalidDayOfWeek(
          now);
    } else if (interviewerTimeSlot.getWeekNum() != WeekService.getNextWeekNum()) {
      throw BadRequestException.invalidWeekNum(interviewerTimeSlot.getWeekNum());
    }
  }

  private static boolean isWeekend(DayOfWeek dayOfWeek) {
    return dayOfWeek.getValue() > DayOfWeek.FRIDAY.getValue();
  }
}
