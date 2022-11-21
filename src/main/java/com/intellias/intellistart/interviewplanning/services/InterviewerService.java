package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
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
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
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
   * @param interviewerId      id of interviewer to bind slot to
   * @param interviewerSlotDto dto to validate and save
   * @return slot
   */
  public InterviewerSlotDto createSlot(Long interviewerId,
      InterviewerSlotDto interviewerSlotDto) {
    User interviewer = userRepository.getReferenceById(interviewerId);
    InterviewerTimeSlot interviewerTimeSlot = InterviewerSlotMapper.mapToEntity(interviewer,
        interviewerSlotDto);
    slotValidator.validate(interviewerTimeSlot);
    return InterviewerSlotMapper.mapToDto(
        interviewerTimeSlotRepository.saveAndFlush(interviewerTimeSlot));
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
  public List<InterviewerSlotDto> getRelevantInterviewerSlots(Long interviewerId) {
    if (!userRepository.existsById(interviewerId)) {
      throw NotFoundException.interviewer(interviewerId);
    }
    List<InterviewerTimeSlot> slots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(
            interviewerId, weekService.getCurrentWeekNum());

    return getInterviewerSlotsWithBookings(slots);
  }

  /**
   * Returns interviewer time slots for the specified week.
   *
   * @param interviewerId id of interviewer
   * @param weekId        id of week
   * @return a list of interviewer time slots
   * @throws NotFoundException if no interviewer is found
   */
  public List<InterviewerSlotDto> getSlotsByWeekId(Long interviewerId, int weekId) {
    User user = userRepository.findById(interviewerId)
        .orElseThrow(() -> NotFoundException.user(interviewerId));

    if (user.getRole() != UserRole.INTERVIEWER) {
      throw NotFoundException.interviewer(interviewerId);
    }

    List<InterviewerTimeSlot> slots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(interviewerId, weekId);

    return getInterviewerSlotsWithBookings(slots);
  }

  /**
   * Returns interviewer slots with bookings.
   *
   * @param slots interviewer time slots
   * @return a list of interviewer time slots with bookings
   */
  public List<InterviewerSlotDto> getInterviewerSlotsWithBookings(List<InterviewerTimeSlot> slots) {
    return slots.stream()
        .map(slot -> InterviewerSlotMapper.mapToDtoWithBookings(slot,
            bookingRepository.findByInterviewerSlot(slot)))
        .sorted(Comparator.comparing((InterviewerSlotDto dto) ->
                DayOfWeek.from(Utils.DAY_OF_WEEK_FORMATTER.parse(dto.getDayOfWeek().toString())))
            .thenComparing(InterviewerSlotDto::getFrom))
        .collect(Collectors.toList());
  }

  /**
   * Update slot by id.
   *
   * @param slotId             slot id
   * @param interviewerSlotDto slot dto
   * @return updated slot
   */
  public InterviewerSlotDto updateSlot(Long interviewerId, Long slotId,
      InterviewerSlotDto interviewerSlotDto) {
    User interviewer = userRepository.getReferenceById(interviewerId);
    InterviewerTimeSlot interviewerTimeSlot = InterviewerSlotMapper.mapToEntity(interviewer,
        interviewerSlotDto);
    slotValidator.validate(interviewerTimeSlot);
    InterviewerTimeSlot slot = getSlotById(slotId);
    if (!slot.getInterviewer().equals(interviewer)) {
      throw NotFoundException.timeSlot(slotId, interviewerId);
    }
    slot.setFrom(interviewerTimeSlot.getFrom());
    slot.setTo(interviewerTimeSlot.getTo());
    slot.setDayOfWeek(interviewerTimeSlot.getDayOfWeek());
    slot.setWeekNum(interviewerTimeSlot.getWeekNum());
    slot.setInterviewer(interviewer);
    return InterviewerSlotMapper.mapToDto(interviewerTimeSlotRepository.save(slot));
  }

  /**
   * Deletes slot by interviewer time slot id.
   *
   * @param interviewerId id of interviewer
   * @param slotId        id of interviewer time slot
   * @param currentUser   current user
   * @return deleted slot
   */
  public InterviewerSlotDto deleteSlot(Long interviewerId, Long slotId, User currentUser) {
    InterviewerTimeSlot slot = getSlotById(slotId);
    if (!slot.getInterviewer().getId().equals(interviewerId)) {
      throw new ApplicationErrorException(ErrorCode.SLOT_NOT_FOUND,
          "Slot of given id does not belong to specified interviewer");
    }
    if (currentUser.getRole() == UserRole.INTERVIEWER) {
      slotValidator.validate(slot);
    }
    if (hasBooking(slot)) {
      throw new ApplicationErrorException(ErrorCode.DELETE_SLOT_WITH_BOOKING);
    }
    interviewerTimeSlotRepository.delete(slot);
    return InterviewerSlotMapper.mapToDto(slot);
  }

  /**
   * Checks if the interviewer time slot has booking.
   *
   * @param slot interviewer time slot
   * @return true if slot has booking, otherwise - false
   */
  private boolean hasBooking(InterviewerTimeSlot slot) {
    return !bookingRepository.findByInterviewerSlot(slot).isEmpty();
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
