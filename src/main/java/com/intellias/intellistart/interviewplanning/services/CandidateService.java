package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
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

  /**
   * Create slot for candidate. Candidate slot must be in the future.
   *
   * @param email            candidate email
   * @param candidateSlotDto candidate slot dto
   * @return slot
   */
  public CandidateSlotDto createSlot(String email, CandidateSlotDto candidateSlotDto) {
    CandidateTimeSlot candidateSlot = CandidateSlotMapper.mapToEntity(email, candidateSlotDto);
    PeriodValidator.validate(candidateSlotDto.getFrom(), candidateSlotDto.getTo());
    PeriodValidator.validateCandidateSlotOverlapping(
        candidateSlotDto.getFrom(),
        candidateSlotDto.getTo(),
        candidateSlotDto.getDate(),
        candidateTimeSlotRepository.findByDateAndEmail(
            candidateSlotDto.getDate(),
            email
        ));
    return CandidateSlotMapper.mapToDto(candidateTimeSlotRepository.save(candidateSlot));
  }

  /**
   * Get candidate time slot by id.
   *
   * @param id slot id
   * @return slotById
   */
  public CandidateTimeSlot getSlot(Long id) {
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
  public List<CandidateSlotDto> getCandidateSlotsWithBookings(List<CandidateTimeSlot> slots) {
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
    // TODO Candidate can update only if there is no bookings
    CandidateTimeSlot timeSlot = candidateTimeSlotRepository.findById(slotId)
        .orElseThrow(() -> NotFoundException.timeSlot(slotId));
    if (!timeSlot.getEmail().equalsIgnoreCase(email)) {
      throw NotFoundException.timeSlot(slotId, email);
    }
    PeriodValidator.validate(candidateSlotDto.getFrom(), candidateSlotDto.getTo());
    PeriodValidator.validateCandidateSlotOverlapping(
        candidateSlotDto.getFrom(),
        candidateSlotDto.getTo(),
        candidateSlotDto.getDate(),
        candidateTimeSlotRepository.findByDateAndEmail(
            candidateSlotDto.getDate(),
            email
        ).stream().filter(s -> !s.getId().equals(slotId)).collect(Collectors.toList())
    );

    timeSlot.setFrom(candidateSlotDto.getFrom());
    timeSlot.setTo(candidateSlotDto.getTo());
    timeSlot.setDate(candidateSlotDto.getDate());
    return CandidateSlotMapper.mapToDto(candidateTimeSlotRepository.save(timeSlot));
  }

}
