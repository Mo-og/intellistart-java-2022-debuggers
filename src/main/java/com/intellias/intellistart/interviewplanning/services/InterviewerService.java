package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Interview service.
 */
@Service
public class InterviewerService {

  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final UserRepository userRepository;

  /**
   * Constructor.
   *
   * @param interviewerTimeSlotRepository time slot repository bean
   * @param userRepository                user repository bean
   */
  @Autowired
  public InterviewerService(InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      UserRepository userRepository) {
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.userRepository = userRepository;
  }

  /**
   * Create slot for interview. Interviewer can create slot for next week.
   *
   * @param interviewerId       id of interviewer to bind slot to
   * @param interviewerTimeSlot slot to validate and save
   * @return slot
   */
  public InterviewerTimeSlot createSlot(Long interviewerId,
      InterviewerTimeSlot interviewerTimeSlot) {
    //todo validation of slot
    User interviewer = userRepository.getReferenceById(interviewerId);
    interviewerTimeSlot.setInterviewer(interviewer);
    return interviewerTimeSlotRepository.saveAndFlush(interviewerTimeSlot);
  }

  /**
   * Get slot by id.
   *
   * @param id slot id
   * @return slotById
   */
  public InterviewerTimeSlot getSlotById(Long id) {
    return interviewerTimeSlotRepository.getReferenceById(id);
  }

  /**
   * Provides time slots for given user for current week and onwards.
   *
   * @param interviewerId id of interviewer to get slots from
   * @return time slots of requested interviewer for current week and future weeks
   */
  public Set<InterviewerTimeSlot> getRelevantInterviewerSlots(Long interviewerId) {
    if (!userRepository.existsById(interviewerId)) {
      throw new InterviewerNotFoundException(interviewerId);
    }
    return interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(
            interviewerId, WeekService.getCurrentWeekNum());
  }

  /**
   * Returns interviewer time slots for the specified week.
   *
   * @param interviewerId id of interviewer
   * @param weekId        id of week
   * @return a set of interviewer time slots
   * @throws InterviewerNotFoundException if no interviewer is found
   */
  public Set<InterviewerTimeSlot> getSlotsByWeekId(Long interviewerId, int weekId) {
    if (!userRepository.existsByIdAndRole(interviewerId, UserRole.INTERVIEWER)) {
      throw new InterviewerNotFoundException(interviewerId);
    }
    return interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(interviewerId, weekId);
  }

  /**
   * Update slot by id.
   *
   * @param slotId              slot id
   * @param interviewerTimeSlot slot
   * @return updated slot
   */
  public InterviewerTimeSlot updateSlot(Long interviewerId, Long slotId,
      InterviewerTimeSlot interviewerTimeSlot) {
    // validate from, to, day, weekNum
    // check if current time is by end of Friday (00:00) of current week
    User interviewer = userRepository.getReferenceById(interviewerId);
    InterviewerTimeSlot slot = getSlotById(slotId);
    slot.setFrom(interviewerTimeSlot.getFrom());
    slot.setTo(interviewerTimeSlot.getTo());
    slot.setDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    slot.setWeekNum(interviewerTimeSlot.getWeekNum());
    slot.setInterviewer(interviewer);
    return interviewerTimeSlotRepository.save(slot);
  }

  /**
   * Gets interviewer from database by id and throws an exception if none found.
   *
   * @param id interviewer id to look for
   * @return interviewer stored by given id
   */
  public User getById(Long id) {
    try {
      return (User) Hibernate.unproxy(userRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw new InterviewerNotFoundException(id);
    }
  }
}
