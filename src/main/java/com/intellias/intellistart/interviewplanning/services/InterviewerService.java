package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import com.intellias.intellistart.interviewplanning.utils.mappers.InterviewerSlotMapper;
import com.intellias.intellistart.interviewplanning.validators.InterviewerSlotValidator;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

/**
 * Interview service.
 */
@Service
@RequiredArgsConstructor
public class InterviewerService {

  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final WeekService weekService;
  private final InterviewerSlotValidator slotValidator;

  /**
   * Create slot for interview. Interviewer can create slot for current or next week.
   *
   * @param interviewerId       id of interviewer to bind slot to
   * @param interviewerTimeSlot slot to validate and save
   * @return slot
   */
  public InterviewerTimeSlot createSlot(Long interviewerId,
      InterviewerTimeSlot interviewerTimeSlot) {
    slotValidator.validate(interviewerTimeSlot);
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
      throw NotFoundException.interviewer(interviewerId);
    }
    return interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(
            interviewerId, weekService.getCurrentWeekNum());
  }

  /**
   * Returns interviewer time slots for the specified week.
   *
   * @param interviewerId id of interviewer
   * @param weekId        id of week
   * @return a set of interviewer time slots
   * @throws NotFoundException if no interviewer is found
   */
  public Set<InterviewerSlotDto> getSlotsByWeekId(Long interviewerId, int weekId) {
    if (!userRepository.existsByIdAndRole(interviewerId, UserRole.INTERVIEWER)) {
      throw NotFoundException.interviewer(interviewerId);
    }

    Set<InterviewerTimeSlot> slots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(interviewerId, weekId);

    return getInterviewerSlotsWithBookings(slots);
  }

  /**
   * Returns interviewer slots with bookings.
   *
   * @param slots interviewer time slots
   * @return a set of interviewer time slots with bookings
   */
  public Set<InterviewerSlotDto> getInterviewerSlotsWithBookings(Set<InterviewerTimeSlot> slots) {
    return slots.stream()
        .map(slot -> InterviewerSlotMapper.mapToDtoWithBookings(slot,
            bookingRepository.findByInterviewerSlot(slot)))
        .collect(Collectors.toCollection(
            () -> new TreeSet<>(Comparator.comparing((InterviewerSlotDto dto) ->
                    DayOfWeek.from(Utils.DAY_OF_WEEK_FORMATTER.parse(dto.getDayOfWeek())))
                .thenComparing(InterviewerSlotDto::getFrom))));
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
    slotValidator.validate(interviewerTimeSlot);
    User interviewer = userRepository.getReferenceById(interviewerId);
    InterviewerTimeSlot slot = getSlotById(slotId);
    if (!slot.getInterviewer().equals(interviewer)) {
      throw NotFoundException.timeSlot(slotId, interviewerId);
    }
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
      throw NotFoundException.interviewer(id);
    }
  }
}
