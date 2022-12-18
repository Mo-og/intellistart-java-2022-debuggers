package com.intellias.intellistart.interviewplanning.validators;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.interfaces.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.interfaces.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.interfaces.Period;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * InterviewerSlotValidate class. Uses to validate interviewer time slot before creating or updating.
 */
@Component
@RequiredArgsConstructor
public class SlotValidator {

  private final WeekService weekService;

  /**
   * Validate interviewer time slot to be created.
   *
   * @param interviewerTimeSlot interviewer time slot
   * @throws InvalidInputException if editing slot no to yhe next week
   * @throws InvalidInputException if editing slot on weekend
   */
  public void validateInterviewerSlot(InterviewerTimeSlot interviewerTimeSlot) {
    DayOfWeek today = weekService.getCurrentDay();
    if (isWeekend(today)) {
      throw InvalidInputException.dayOfWeek(today);
    } else if (interviewerTimeSlot.getWeekNum() != weekService.getNextWeekNum()) {
      throw InvalidInputException.slotWeekNum(interviewerTimeSlot.getWeekNum());
    }
  }

  private static boolean isWeekend(DayOfWeek dayOfWeek) {
    return dayOfWeek.getValue() > DayOfWeek.FRIDAY.getValue();
  }

  /**
   * Validate candidate time slot to be created or updated.
   *
   * @param candidateTimeSlot candidate time slot
   * @throws InvalidInputException if editing slot on weekend
   */
  public void validateCandidateSlot(CandidateTimeSlot candidateTimeSlot) {
    LocalDateTime now = weekService.getCurrentDateTime();
    LocalDateTime slotDateTime = LocalDateTime.of(candidateTimeSlot.getDate(),
        candidateTimeSlot.getFrom());
    if (slotDateTime.isBefore(now)) {
      throw InvalidInputException.dateTime();
    }
  }

  /**
   * Method to validate time slot overlapping.
   *
   * @param slot     time slot
   * @param allSlots list of slots
   */
  public static void validateSlotOverlapping(Period slot, List<? extends Period> allSlots) {
    allSlots.forEach(s -> {
      if (!(s.isAfterOrEqual(slot) || s.isBeforeOrEqual(slot))) {
        throw InvalidInputException.periodOverlapping();
      }
    });
  }

}
