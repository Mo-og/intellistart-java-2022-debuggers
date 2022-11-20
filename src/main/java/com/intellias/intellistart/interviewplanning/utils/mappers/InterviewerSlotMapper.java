package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Interviewer slot mapper.
 */
@UtilityClass
public class InterviewerSlotMapper {

  /**
   * Maps to InterviewerSlotDto with bookings.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return InterviewerSlotDto with bookings
   */
  public InterviewerSlotDto mapToDtoWithBookings(InterviewerTimeSlot slot, List<Booking> bookings) {
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

  /**
   * Maps to InterviewerSlotDto.
   *
   * @param slot entity
   * @return InterviewerSlotDto
   */
  public InterviewerSlotDto mapToDto(InterviewerTimeSlot slot) {
    if (slot == null) {
      return null;
    }
    return InterviewerSlotDto.builder()
        .id(slot.getId())
        .weekNum(slot.getWeekNum())
        .dayOfWeek(slot.getShortDayOfWeek())
        .from(slot.getFrom())
        .to(slot.getTo())
        .build();
  }
}
