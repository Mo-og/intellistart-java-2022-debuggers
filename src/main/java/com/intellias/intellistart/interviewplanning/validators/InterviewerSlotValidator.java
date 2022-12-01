package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.DayOfWeek;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * InterviewerSlotValidate class. Used to validate interviewer time slot before creating or updating.
 */
@Component
@RequiredArgsConstructor
public class InterviewerSlotValidator {

  private final WeekService weekService;

  /**
   * Validate interviewer time slot to be created.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @throws InvalidInputException if editing slot no to yhe next week
   * @throws InvalidInputException if editing slot on weekend
   */
  public void validate(InterviewerTimeSlot interviewerTimeSlot) {
    var now = weekService.getNowDay();
    if (isWeekend(now)) {
      throw InvalidInputException.dayOfWeek(now);
    } else if (interviewerTimeSlot.getWeekNum() != weekService.getNextWeekNum()) {
      throw InvalidInputException.slotWeekNum(interviewerTimeSlot.getWeekNum());
    }
  }

  private static boolean isWeekend(DayOfWeek dayOfWeek) {
    return dayOfWeek.getValue() > DayOfWeek.FRIDAY.getValue();
  }
}
