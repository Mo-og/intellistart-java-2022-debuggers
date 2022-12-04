package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import java.time.DayOfWeek;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerSlotValidateTest {

  private final WeekServiceImp weekService = Mockito.mock(WeekServiceImp.class);

  private final InterviewerSlotValidator interviewerSlotValidator = new InterviewerSlotValidator(weekService);

  private final InterviewerTimeSlot interviewerTimeSlot = new InterviewerTimeSlot("10:00", "11:00", "MONDAY", 1);


  @Test
  void validateSlotOnWeekendTest() {
    when(weekService.getNowDay()).thenReturn(DayOfWeek.MONDAY);
    when(weekService.getNextWeekNum()).thenReturn(1);
    assertDoesNotThrow(() -> interviewerSlotValidator.validate(interviewerTimeSlot));
  }

  @Test
  void validateSlotOnWeekdayTest() {
    when(weekService.getNowDay()).thenReturn(DayOfWeek.SUNDAY);
    when(weekService.getNextWeekNum()).thenReturn(1);
    assertThrows(InvalidInputException.class, () -> interviewerSlotValidator.validate(interviewerTimeSlot));
  }

}
