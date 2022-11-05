package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 * Interviewer slot mapper.
 */
@UtilityClass
public class InterviewerSlotMapper {

  /**
   * to InterviewerSlotDto.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return InterviewerSlotDto
   */
  public InterviewerSlotDto mapToDtoWithBookings(InterviewerTimeSlot slot, Set<Booking> bookings) {
    if (slot == null) {
      return null;
    }
    return InterviewerSlotDto.builder()
        .id(slot.getId())
        .weekNum(slot.getWeekNum())
        .dayOfWeek(slot.getShortDayOfWeek())
        .from(slot.getFrom())
        .to(slot.getTo())
        .bookings(BookingMapper.mapSetToDto(bookings))
        .build();
  }
}
