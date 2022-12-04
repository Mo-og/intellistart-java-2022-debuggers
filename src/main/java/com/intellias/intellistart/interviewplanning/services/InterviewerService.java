package com.intellias.intellistart.interviewplanning.services;

import static com.intellias.intellistart.interviewplanning.validators.SlotValidator.validateSlotOverlapping;

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
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
import com.intellias.intellistart.interviewplanning.validators.SlotValidator;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
  private final SlotValidator slotValidator;

  /**
   * Create slot for interview. Interviewer can create slot for current or next week.
   *
   * @param interviewerId      id of interviewer to bind slot to
   * @param interviewerSlotDto dto to validate and save
   * @return slot
   */
  public InterviewerSlotDto createSlot(Long interviewerId, InterviewerSlotDto interviewerSlotDto) {
    User interviewer = getInterviewerById(interviewerId);
    InterviewerTimeSlot slot = InterviewerSlotMapper.mapToEntity(interviewer, interviewerSlotDto);
    slotValidator.validateInterviewerSlot(interviewerSlotDto);
    PeriodValidator.validate(interviewerSlotDto);
    validateSlotOverlapping(interviewerSlotDto,
        interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(interviewerId, slot.getWeekNum())
            .stream()
            .filter(s -> s.getDayOfWeek().equals(slot.getDayOfWeek()))
            .collect(Collectors.toList()));

    return InterviewerSlotMapper.mapToDto(interviewerTimeSlotRepository.save(slot));
  }

  /**
   * Provides time slots for given user for current week and onwards.
   *
   * @param interviewerId id of interviewer to get slots from
   * @return time slots of requested interviewer for current week and future weeks
   * @throws NotFoundException if no interviewer found
   */
  public List<InterviewerSlotDto> getRelevantInterviewerSlots(Long interviewerId) {
    User interviewer = getInterviewerById(interviewerId);

    List<InterviewerTimeSlot> slots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(
            interviewer.getId(), weekService.getCurrentWeekNum());

    return getInterviewerSlotsWithBookings(slots);
  }

  /**
   * Provides interviewer time slots for the specified week.
   *
   * @param interviewerId id of interviewer
   * @param weekId        id of week
   * @return a list of interviewer time slots
   * @throws NotFoundException if no interviewer is found
   */
  public List<InterviewerSlotDto> getSlotsByWeekId(Long interviewerId, int weekId) {
    User interviewer = getInterviewerById(interviewerId);

    List<InterviewerTimeSlot> slots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(interviewer.getId(), weekId);

    return getInterviewerSlotsWithBookings(slots);
  }

  /**
   * Provides interviewer slots with bookings.
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
   * Updates interviewer slot by slot id.
   *
   * @param interviewerId      interviewer id
   * @param slotId             slot id
   * @param interviewerSlotDto interviewer slot dto
   * @return updated slot
   * @throws ApplicationErrorException if slot has active booking
   */
  public InterviewerSlotDto updateSlot(Long interviewerId, Long slotId,
      InterviewerSlotDto interviewerSlotDto) {
    User interviewer = getInterviewerById(interviewerId);
    InterviewerTimeSlot slot = getSlotById(interviewer.getId(), slotId);

    slotValidator.validateInterviewerSlot(interviewerSlotDto);
    slotValidator.validateInterviewerSlot(InterviewerSlotMapper.mapToDto(slot));
    PeriodValidator.validate(interviewerSlotDto);
    SlotValidator.validateSlotOverlapping(
        interviewerSlotDto,
        interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(interviewerId,
                slot.getWeekNum())
            .stream()
            .filter(s -> s.getDayOfWeek().equals(slot.getDayOfWeek()) && !s.getId().equals(slotId))
            .collect(Collectors.toList()));

    if (hasBooking(slot)) {
      throw new ApplicationErrorException(ErrorCode.CANNOT_EDIT_SLOT_WITH_BOOKING);
    }

    slot.setFrom(interviewerSlotDto.getFrom());
    slot.setTo(interviewerSlotDto.getTo());
    slot.setDayOfWeek(interviewerSlotDto.getDayOfWeek());
    slot.setWeekNum(interviewerSlotDto.getWeekNum());
    slot.setInterviewer(interviewer);

    return InterviewerSlotMapper.mapToDto(interviewerTimeSlotRepository.save(slot));
  }

  /**
   * Deletes slot by interviewer time slot id.
   *
   * @param userId      id of interviewer
   * @param slotId      id of interviewer time slot
   * @param currentUser current user
   * @return deleted slot
   * @throws ApplicationErrorException if slot has active booking
   */
  public InterviewerSlotDto deleteSlot(Long userId, Long slotId, User currentUser) {
    User interviewer = getInterviewerById(userId);
    InterviewerTimeSlot slot = getSlotById(interviewer.getId(), slotId);

    if (currentUser.getRole() != UserRole.COORDINATOR) {
      slotValidator.validateInterviewerSlot(InterviewerSlotMapper.mapToDto(slot));
    }

    if (hasBooking(slot)) {
      throw new ApplicationErrorException(ErrorCode.CANNOT_EDIT_SLOT_WITH_BOOKING);
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
   * Provides slot by slot id.
   *
   * @param userId id of interviewer
   * @param slotId id of interviewer slot
   * @return interviewer slot
   * @throws NotFoundException if no interviewer slot found
   */
  public InterviewerTimeSlot getSlotById(Long userId, Long slotId) {
    InterviewerTimeSlot slot = interviewerTimeSlotRepository.findById(slotId)
        .orElseThrow(() -> NotFoundException.timeSlot(slotId));

    if (!slot.getInterviewer().getId().equals(userId)) {
      throw NotFoundException.timeSlot(slotId, userId);
    }

    return slot;
  }

  /**
   * Provides interviewer by id.
   *
   * @param userId id of interviewer
   * @return interviewer
   * @throws NotFoundException if no interviewer found
   */
  public User getInterviewerById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> NotFoundException.user(userId));

    if (user.getRole() != UserRole.INTERVIEWER) {
      throw NotFoundException.interviewer(userId);
    }

    return user;
  }

}
