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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Candidate service.
 */
@Service
public class CandidateService {

  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   *
   * @param candidateTimeSlotRepository time slot repository bean
   * @param bookingRepository           booking repository bean
   */
  @Autowired
  public CandidateService(CandidateTimeSlotRepository candidateTimeSlotRepository,
      BookingRepository bookingRepository) {
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Create slot for candidate. Candidate slot must be in the future.
   *
   * @param email            candidate email
   * @param candidateSlotDto candidate slot dto
   * @return slot
   */

  public CandidateSlotDto createSlot(String email, CandidateSlotDto candidateSlotDto) {
    //todo validation of slot
    CandidateTimeSlot candidateSlot = CandidateSlotMapper.mapToEntity(email, candidateSlotDto);
    return CandidateSlotMapper.mapToDto(candidateTimeSlotRepository.save(candidateSlot));
  }

  /**
   * Get slot by id.
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
  public Set<CandidateSlotDto> getAllCandidateSlots(String email) {
    return getCandidateSlotsWithBookings(candidateTimeSlotRepository.findByEmail(email));
  }

  /**
   * Returns candidate slots with bookings.
   *
   * @param slots candidate time slots
   * @return a set of candidate time slots with bookings
   */
  public Set<CandidateSlotDto> getCandidateSlotsWithBookings(List<CandidateTimeSlot> slots) {
    return slots.stream()
        .map(slot -> CandidateSlotMapper
            .mapToDtoWithBookings(slot, bookingRepository.findByCandidateSlot(slot)))
        .collect(Collectors.toCollection(
            () -> new TreeSet<>(Comparator.comparing(CandidateSlotDto::getDate)
                .thenComparing(CandidateSlotDto::getFrom))));
  }

  /**
   * Update slot by id.
   *
   * @param slotId slot id
   * @param slot   candidate time slot
   */
  public CandidateTimeSlot updateSlot(Long slotId, CandidateTimeSlot slot) {
    PeriodValidator.validate(slot.getFrom(), slot.getTo());
    // check if current time is by end of Friday (00:00) of current week
    if (!candidateTimeSlotRepository.existsById(slotId)) {
      throw NotFoundException.timeSlot(slotId);
    }
    CandidateTimeSlot timeSlot = candidateTimeSlotRepository.getReferenceById(slotId);
    timeSlot.setFrom(slot.getFrom());
    timeSlot.setTo(slot.getTo());
    timeSlot.setDate(slot.getDate());
    return candidateTimeSlotRepository.save(slot);
  }

}