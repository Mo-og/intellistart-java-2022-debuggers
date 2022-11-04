package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekService;
import com.intellias.intellistart.interviewplanning.validators.InterviewerSlotValidator.Action;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InterviewerSlotValidateTest {

  private static final InterviewerTimeSlot currentWeekSlot;
  private static final InterviewerTimeSlot nextWeekSlot;

  static {
    currentWeekSlot = new InterviewerTimeSlot(
        "08:00", "22:00", "MONDAY", WeekService.getCurrentWeekNum());

    nextWeekSlot = new InterviewerTimeSlot(
        "08:00", "22:00", "MONDAY", WeekService.getNextWeekNum());
  }

  @Test
  void validateSlotToBeUpdated() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      assertDoesNotThrow(() -> InterviewerSlotValidator.validate(nextWeekSlot, Action.UPDATE));
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> InterviewerSlotValidator.validate(nextWeekSlot, Action.UPDATE));
    }

    assertThrows(ApplicationErrorException.class,
        () -> InterviewerSlotValidator.validate(currentWeekSlot, Action.UPDATE));
  }

  @Test
  void validateSlotToBeCreated() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      assertDoesNotThrow(() -> InterviewerSlotValidator.validate(nextWeekSlot, Action.CREATE));
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> InterviewerSlotValidator.validate(nextWeekSlot, Action.CREATE));
    }

    assertThrows(ApplicationErrorException.class,
        () -> InterviewerSlotValidator.validate(currentWeekSlot, Action.CREATE));
  }

}
