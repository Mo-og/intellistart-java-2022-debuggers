package com.intellias.intellistart.interviewplanning.utils.mappers;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.User;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Interviewer slot mapper.
 */
@UtilityClass
public class InterviewerSlotMapper {

  /**
   * Maps to InterviewerSlotDto with bookings.
   *
   * @param slot     entity
   * @param bookings BookingDto
   * @return InterviewerSlotDto with bookings
   */
  public InterviewerSlotDto mapToDtoWithBookings(InterviewerTimeSlotImpl slot, List<BookingImpl> bookings) {
    if (slot == null) {
      return null;
    }
    return new InterviewerSlotDto(slot.getId(), slot.getWeekNum(),
        slot.getShortDayOfWeek(), slot.getFrom(), slot.getTo(),
        BookingMapper.mapSetToDto(bookings));
  }

  /**
   * to InterviewerSlotDto.
   *
   * @param slot entity
   * @return InterviewerSlotDto
   */
  public InterviewerSlotDto mapToDto(InterviewerTimeSlotImpl slot) {
    if (slot == null) {
      return null;
    }
    return new InterviewerSlotDto(slot.getId(), slot.getWeekNum(),
        slot.getShortDayOfWeek(), slot.getFrom(), slot.getTo());
  }

  /**
   * to InterviewerTimeSlot.
   *
   * @param interviewer our interviewer
   * @param slotDto     slot dto
   * @return InterviewerSlotDto
   */
  public InterviewerTimeSlotImpl mapToEntity(User interviewer, InterviewerSlotDto slotDto) {
    if (slotDto == null) {
      return null;
    }
    InterviewerTimeSlotImpl interviewerSlot = new InterviewerTimeSlotImpl();
    interviewerSlot.setInterviewer(interviewer);
    interviewerSlot.setDayOfWeek(slotDto.getDayOfWeek());
    interviewerSlot.setWeekNum(slotDto.getWeekNum());
    interviewerSlot.setFrom(slotDto.getFrom());
    interviewerSlot.setTo(slotDto.getTo());
    interviewerSlot.setId(slotDto.getId());
    return interviewerSlot;
  }
}
