package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Interviewer slot mapper.
 */
@Component
public class InterviewerSlotMapper {

  /**
   * to InterviewerSlotDto.
   *
   * @param slot entity
   * @param bookings BookingDto
   * @return InterviewerSlotDto
   */
  public InterviewerSlotDto mapToInterviewerSlotWithBookingsDto(
      InterviewerTimeSlot slot, Set<BookingDto> bookings) {
    return InterviewerSlotDto.builder()
        .id(slot.getId())
        .weekNum(slot.getWeekNum())
        .dayOfWeek(slot.getShortDayOfWeek())
        .from(slot.getFrom())
        .to(slot.getTo())
        .bookings(bookings)
        .build();
  }
}
