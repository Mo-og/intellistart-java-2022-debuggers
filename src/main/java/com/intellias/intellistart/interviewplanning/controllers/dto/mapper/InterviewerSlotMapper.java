package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Interviewer slot mapper.
 */
@Component
public class InterviewerSlotMapper {

  private final BookingMapper bookingMapper;

  @Autowired
  public InterviewerSlotMapper(BookingMapper bookingMapper) {
    this.bookingMapper = bookingMapper;
  }

  /**
   * to InterviewerSlotDto.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return InterviewerSlotDto
   */
  public InterviewerSlotDto mapToInterviewerSlotWithBookingsDto(
      InterviewerTimeSlot slot, Set<Booking> bookings) {
    if (slot == null) {
      return null;
    }
    return InterviewerSlotDto.builder()
        .id(slot.getId())
        .weekNum(slot.getWeekNum())
        .dayOfWeek(slot.getShortDayOfWeek())
        .from(slot.getFrom())
        .to(slot.getTo())
        .bookings(bookingMapper.mapToBookingSetDto(bookings))
        .build();
  }
}
