package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.CandidateSlotMapper;
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Candidate service.
 */
@Service
public class CandidateService {

  private final CandidateTimeSlotRepository candidateTimeSlotRepository;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   *
   * @param candidateTimeSlotRepository time slot repository bean
   * @param userRepository              user repository bean
   * @param bookingRepository           booking repository bean
   */
  @Autowired
  public CandidateService(CandidateTimeSlotRepository candidateTimeSlotRepository,
      UserRepository userRepository, BookingRepository bookingRepository) {
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.userRepository = userRepository;
    this.bookingRepository = bookingRepository;
  }

  /**
   * Create slot for candidate. Candidate slot must be in the future.
   *
   * @param candidateId      id of candidate to bind slot to
   * @param candidateSlotDto candidate slot dto
   * @return slot
   */
  public CandidateSlotDto createSlot(Long candidateId, CandidateSlotDto candidateSlotDto) {
    PeriodValidator.validate(candidateSlotDto.getFrom(), candidateSlotDto.getTo());
    User candidate = userRepository.getReferenceById(candidateId);
    CandidateTimeSlot candidateSlot = CandidateSlotMapper.mapToEntity(candidateSlotDto, candidate);
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
   * @param candidateId id of candidate to get slots from
   * @return time slots of requested candidate
   */
  public Set<CandidateSlotDto> getAllCandidateSlots(Long candidateId) {
    if (!userRepository.existsByIdAndRole(candidateId, UserRole.CANDIDATE)) {
      throw NotFoundException.candidate(candidateId);
    }
    return getCandidateSlotsWithBookings(candidateTimeSlotRepository.findAll());
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

  /**
   * Gets candidate from database by id and throws an exception if none found.
   *
   * @param id candidate id to look for
   * @return candidate stored by given id
   */
  public User getById(Long id) {
    try {
      return (User) Hibernate.unproxy(userRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw NotFoundException.candidate(id);
    }
  }

}