package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.util.Set;
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

  /**
   * Constructor.
   *
   * @param candidateTimeSlotRepository time slot repository bean
   * @param userRepository              user repository bean
   */
  @Autowired
  public CandidateService(CandidateTimeSlotRepository candidateTimeSlotRepository,
      UserRepository userRepository) {
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
    this.userRepository = userRepository;
  }


  /**
   * Create slot for candidate. Candidate can create slot for next week.
   *
   * @param candidateId       id of candidate to bind slot to
   * @param candidateTimeSlot slot to validate and save
   * @return slot
   */
  public CandidateTimeSlot createSlot(Long candidateId,
      CandidateTimeSlot candidateTimeSlot) {
    //todo validation of slot
    User candidate = userRepository.getReferenceById(candidateId);
    candidateTimeSlot.setCandidate(candidate);
    return candidateTimeSlotRepository.saveAndFlush(candidateTimeSlot);

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
   * Provides time slots for given user for current week and onwards.
   *
   * @param candidateId id of interviewer to get slots from
   * @return time slots of requested candidate for current week and future weeks
   */
  public Set<CandidateTimeSlot> getRelevantCandidateSlots(Long candidateId) {
    if (!userRepository.existsById(candidateId)) {
      throw NotFoundException.candidate(candidateId);
    }
    return candidateTimeSlotRepository
        .getCandidateTimeSlotForCandidateIdAndWeekGreaterOrEqual(
            candidateId, WeekService.getCurrentDate());
  }

  /**
   * Update slot by id.
   *
   * @param slotId slot id
   * @param slot   candidate time slot
   */
  public CandidateTimeSlot updateSlot(Long slotId, CandidateTimeSlot slot) {
    // validate from, to, date
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
