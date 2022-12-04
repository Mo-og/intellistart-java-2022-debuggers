package com.intellias.intellistart.interviewplanning.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.WeekServiceImp;
import com.intellias.intellistart.interviewplanning.utils.mappers.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.utils.mappers.InterviewerSlotMapper;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.intellias.intellistart.interviewplanning.validators.SlotValidator.validateSlotOverlapping;

@ExtendWith(MockitoExtension.class)
class SlotValidatorTest {

  private final WeekServiceImp weekService = Mockito.mock(WeekServiceImp.class);

  private final SlotValidator slotValidator = new SlotValidator(weekService);

  private final InterviewerTimeSlot interviewerTimeSlot = new InterviewerTimeSlot("10:00", "11:00", "MONDAY", 1);
  private final CandidateTimeSlot candidateTimeSlot = new CandidateTimeSlot("test@mail.com", "2022-12-12", "10:00", "11:00");


  @Test
  void validateInterviewerSlotOnWeekendTest() {
    when(weekService.getCurrentDay()).thenReturn(DayOfWeek.MONDAY);
    when(weekService.getNextWeekNum()).thenReturn(1);
    assertDoesNotThrow(() -> slotValidator.validateInterviewerSlot(
        InterviewerSlotMapper.mapToDto(interviewerTimeSlot)
    ));
  }

  @Test
  void validateInterviewerSlotOnWeekdayTest() {
    when(weekService.getCurrentDay()).thenReturn(DayOfWeek.SUNDAY);
    when(weekService.getNextWeekNum()).thenReturn(1);
    assertThrows(InvalidInputException.class,
        () -> slotValidator.validateInterviewerSlot(
            InterviewerSlotMapper.mapToDto(interviewerTimeSlot)
        ));
  }

  @Test
  void validateCandidateSlotInFuture() {
    when(weekService.getCurrentDateTime()).thenReturn(LocalDateTime.of(2022, 12, 11, 0, 0));
    assertDoesNotThrow(() -> slotValidator.validateCandidateSlot(
        CandidateSlotMapper.mapToDto(candidateTimeSlot)
    ));
  }

  @Test
  void validateCandidateSlotInPast() {
    when(weekService.getCurrentDateTime()).thenReturn(LocalDateTime.of(2022, 12, 13, 0, 0));
    assertThrows(InvalidInputException.class,
        () -> slotValidator.validateCandidateSlot(
            CandidateSlotMapper.mapToDto(candidateTimeSlot)
        ));
  }

  @Test
  void validateSlotOverlappingTest() {
    List<InterviewerTimeSlot> slots = List.of(
        new InterviewerTimeSlot("08:00", "09:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("10:30", "12:30", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("12:30", "14:00", "Mon", weekService.getNextWeekNum()),
        new InterviewerTimeSlot("14:30", "17:00", "Mon", weekService.getNextWeekNum())
    );

    assertThrows(InvalidInputException.class,
        () -> validateSlotOverlapping(
            new InterviewerTimeSlot("08:30", "10:30", "Mon", weekService.getNextWeekNum()),
            slots));

    assertThrows(InvalidInputException.class,
        () -> validateSlotOverlapping(
            new InterviewerTimeSlot("08:00", "09:30", "Mon", weekService.getNextWeekNum()),
            slots));

    assertDoesNotThrow(() -> validateSlotOverlapping(
        new InterviewerTimeSlot("17:00", "20:00", "Mon", weekService.getNextWeekNum()),
        slots));
  }

}
