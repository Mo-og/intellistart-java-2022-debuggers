package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Candidate slot mapper.
 */
@Component
public class CandidateSlotMapper {

  /**
   * to CandidateSlotDto.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return CandidateSlotDto
   */
  public CandidateSlotDto mapToCandidateSlotDtoWithBookings(
      CandidateTimeSlot slot, Set<BookingDto> bookings) {
    return CandidateSlotDto.builder()
        .id(slot.getId())
        .from(slot.getFrom())
        .to(slot.getTo())
        .date(slot.getDate())
        .bookings(bookings)
        .build();
  }
}
