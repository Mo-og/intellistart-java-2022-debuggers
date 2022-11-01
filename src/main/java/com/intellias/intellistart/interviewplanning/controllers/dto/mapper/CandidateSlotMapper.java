package com.intellias.intellistart.interviewplanning.controllers.dto.mapper;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Candidate slot mapper.
 */
@Component
public class CandidateSlotMapper {

  private final BookingMapper bookingMapper;

  @Autowired
  public CandidateSlotMapper(BookingMapper bookingMapper) {
    this.bookingMapper = bookingMapper;
  }

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
        .bookings(bookingMapper.mapToBookingSetDto(bookings))
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
