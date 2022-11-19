package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InterviewerSlotValidateTest {

  private final InterviewerTimeSlot currentWeekSlot;
  private final InterviewerTimeSlot nextWeekSlot;
  private final WeekService weekService = new WeekServiceImp();
  private final InterviewerSlotValidator slotValidator = new InterviewerSlotValidator(weekService);

  {
    currentWeekSlot = new InterviewerTimeSlot(
        "08:00", "22:00", "MONDAY", weekService.getCurrentWeekNum());

    nextWeekSlot = new InterviewerTimeSlot(
        "08:00", "22:00", "MONDAY", weekService.getNextWeekNum());
  }

  @Test
  void validateSlotToBeUpdated() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      assertDoesNotThrow(() -> slotValidator.validate(nextWeekSlot));
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> slotValidator.validate(nextWeekSlot));
    }

    assertThrows(ApplicationErrorException.class,
        () -> slotValidator.validate(currentWeekSlot));
  }

  @Test
  void validateSlotToBeCreated() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      assertDoesNotThrow(() -> slotValidator.validate(nextWeekSlot));
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> slotValidator.validate(nextWeekSlot));
    }

    assertThrows(ApplicationErrorException.class,
        () -> slotValidator.validate(currentWeekSlot));
  }

}
