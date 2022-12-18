package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import java.util.List;
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
  public CandidateSlotDto mapToDtoWithBookings(CandidateTimeSlotImpl slot,
      List<BookingImpl> bookings) {
    if (slot == null) {
      return null;
    }
    return CandidateSlotDto.builder()
        .id(slot.getId())
        .from(slot.getFrom())
        .to(slot.getTo())
        .date(slot.getDate())
        .bookings(BookingMapper.mapSetToDto(bookings))
        .build();
  }

  /**
   * to CandidateSlotDto.
   *
   * @param slot entity
   * @return CandidateSlotDto
   */
  public CandidateSlotDto mapToDto(CandidateTimeSlotImpl slot) {
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
   * @param email   candidate email
   * @param slotDto slot dto
   * @return CandidateSlotDto
   */
  public CandidateTimeSlotImpl mapToEntity(String email, CandidateSlotDto slotDto) {
    if (slotDto == null) {
      return null;
    }
    CandidateTimeSlotImpl candidateSlot = new CandidateTimeSlotImpl();
    candidateSlot.setEmail(email);
    candidateSlot.setDate(slotDto.getDate());
    candidateSlot.setFrom(slotDto.getFrom());
    candidateSlot.setTo(slotDto.getTo());
    candidateSlot.setId(slotDto.getId());
    return candidateSlot;
  }

}
