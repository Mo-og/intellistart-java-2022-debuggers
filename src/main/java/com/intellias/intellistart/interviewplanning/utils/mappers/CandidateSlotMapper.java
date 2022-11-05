package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import java.util.Set;
import lombok.experimental.UtilityClass;

/**
 * Candidate slot mapper.
 */
@UtilityClass
public class CandidateSlotMapper {

  /**
   * to CandidateSlotDto.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return CandidateSlotDto
   */
  public CandidateSlotDto mapToCandidateSlotDtoWithBookings(CandidateTimeSlot slot,
      Set<Booking> bookings) {
    if (slot == null) {
      return null;
    }
    return CandidateSlotDto.builder()
        .id(slot.getId())
        .from(slot.getFrom())
        .to(slot.getTo())
        .date(slot.getDate())
        .bookings(BookingMapper.mapToBookingSetDto(bookings))
        .build();
  }

  /**
   * to CandidateSlotDto.
   *
   * @param slot entity
   * @return CandidateSlotDto
   */
  public CandidateSlotDto mapToCandidateSlotDto(CandidateTimeSlot slot) {
    if (slot == null) {
      return null;
    }
    return CandidateSlotDto.builder()
        .id(slot.getId())
        .from(slot.getFrom())
        .to(slot.getTo())
        .date(slot.getDate())
        .build();
  }

  /**
   * to CandidateTimeSlot.
   *
   * @param slotDto slot dto
   * @return CandidateSlotDto
   */
  public CandidateTimeSlot mapToCandidateSlotEntity(CandidateSlotDto slotDto, User candidate) {
    if (slotDto == null) {
      return null;
    }
    CandidateTimeSlot candidateSlot = new CandidateTimeSlot();
    candidateSlot.setCandidate(candidate);
    candidateSlot.setDate(slotDto.getDate());
    candidateSlot.setFrom(slotDto.getFrom());
    candidateSlot.setTo(slotDto.getTo());
    candidateSlot.setId(slotDto.getId());
    return candidateSlot;
  }
}
