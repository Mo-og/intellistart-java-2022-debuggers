package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
import com.intellias.intellistart.interviewplanning.validators.SlotValidator;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Candidate service.
 */
@Service
@RequiredArgsConstructor
public class CandidateService {

  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;
  private final SlotValidator slotValidator;

  /**
   * Create slot for candidate. Candidate slot must be in the future.
   *
   * @param email            candidate email
   * @param candidateSlotDto candidate slot dto
   * @return slot
   */
  public CandidateSlotDto createSlot(String email, CandidateSlotDto candidateSlotDto) {
    slotValidator.validateCandidateSlot(candidateSlotDto);
    PeriodValidator.validate(candidateSlotDto);
    List<CandidateTimeSlotImpl> slots = candidateTimeSlotRepository.findByDateAndEmail(
        candidateSlotDto.getDate(), email);
    SlotValidator.validateSlotOverlapping(candidateSlotDto, slots);

    return CandidateSlotMapper.mapToDto(candidateTimeSlotRepository.save(
        CandidateSlotMapper.mapToEntity(email, candidateSlotDto)
    ));
  }

  /**
   * Get candidate time slot by id.
   *
   * @param id slot id
   * @return slotById
   */
  public CandidateTimeSlotImpl getSlot(Long id) {
    return candidateTimeSlotRepository.getReferenceById(id);
  }

  /**
   * Provides all time slots for candidate.
   *
   * @param email email of candidate to get slots from
   * @return time slots of requested candidate
   */
  public List<CandidateSlotDto> getAllCandidateSlots(String email) {
    return getCandidateSlotsWithBookings(candidateTimeSlotRepository.findByEmail(email));
  }

  /**
   * Returns candidate slots with bookings.
   *
   * @param slots candidate time slots
   * @return a list of candidate time slots with bookings
   */
  public List<CandidateSlotDto> getCandidateSlotsWithBookings(List<CandidateTimeSlotImpl> slots) {
    return slots.stream()
        .map(slot -> CandidateSlotMapper
            .mapToDtoWithBookings(slot, bookingRepository.findByCandidateSlot(slot)))
        .sorted(Comparator.comparing(CandidateSlotDto::getDate)
            .thenComparing(CandidateSlotDto::getFrom))
        .collect(Collectors.toList());
  }

  /**
   * Update slot by id.
   *
   * @param email            candidate email
   * @param slotId           slot id
   * @param candidateSlotDto candidate time slot dto
   * @throws NotFoundException if no candidate slot is found
   */
  public CandidateSlotDto updateSlot(String email, Long slotId, CandidateSlotDto candidateSlotDto) {
    CandidateTimeSlotImpl timeSlot = candidateTimeSlotRepository.findById(slotId)
        .orElseThrow(() -> NotFoundException.timeSlot(slotId));
    if (!timeSlot.getEmail().equalsIgnoreCase(email)) {
      throw NotFoundException.timeSlot(slotId, email);
    }
    if (hasBooking(timeSlot)) {
      throw new ApplicationErrorException(ErrorCode.CANNOT_EDIT_SLOT_WITH_BOOKING);
    }
    slotValidator.validateCandidateSlot(candidateSlotDto);
    slotValidator.validateCandidateSlot(CandidateSlotMapper.mapToDto(timeSlot));
    PeriodValidator.validate(candidateSlotDto);
    SlotValidator.validateSlotOverlapping(
        candidateSlotDto,
        candidateTimeSlotRepository.findByDateAndEmail(candidateSlotDto.getDate(), email)
            .stream()
            .filter(s -> !s.getId().equals(slotId))
            .collect(Collectors.toList())
    );

    timeSlot.setFrom(candidateSlotDto.getFrom());
    timeSlot.setTo(candidateSlotDto.getTo());
    timeSlot.setDate(candidateSlotDto.getDate());
    return CandidateSlotMapper.mapToDto(candidateTimeSlotRepository.save(timeSlot));
  }

  private boolean hasBooking(CandidateTimeSlotImpl candidateTimeSlot) {
    return !bookingRepository.findByCandidateSlot(candidateTimeSlot).isEmpty();
  }

  /**
   * Delete slot by id.
   *
   * @param email  candidate email
   * @param slotId slot id
   * @return candidate slot dto
   */
  public CandidateSlotDto deleteSlot(String email, Long slotId) {
    CandidateTimeSlotImpl timeSlot = candidateTimeSlotRepository.findById(slotId)
        .orElseThrow(() -> NotFoundException.timeSlot(slotId));
    if (!timeSlot.getEmail().equalsIgnoreCase(email)) {
      throw NotFoundException.timeSlot(slotId, email);
    }
    if (hasBooking(timeSlot)) {
      throw new ApplicationErrorException(ErrorCode.CANNOT_EDIT_SLOT_WITH_BOOKING);
    }
    candidateTimeSlotRepository.delete(timeSlot);
    return CandidateSlotMapper.mapToDto(timeSlot);
  }

}
