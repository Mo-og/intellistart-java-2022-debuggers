package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.TimeSlotRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Interview service.
 */
@Service
public class InterviewerService {

  private final TimeSlotRepository timeSlotRepository;

  /**
   * Constructor.
   *
   * @param timeSlotRepository time slot repository
   */
  @Autowired
  public InterviewerService(TimeSlotRepository timeSlotRepository) {
    this.timeSlotRepository = timeSlotRepository;
  }

  /**
   * Crete slot for interview. Interviewer can create slot for next week.
   *
   * @param from start time
   * @param to   end time
   * @param day  day of week
   * @return slot
   */
  public InterviewerTimeSlot createSlot(String from, String to, String day, int weekNum) {
    // validate from, to, day, weekNum
    // check if current time is by end of Friday (00:00) of current week
    return timeSlotRepository.save(new InterviewerTimeSlot(from, to, day, weekNum));
  }

  /**
   * Get slot by id.
   *
   * @param id slot id
   * @return slotById
   */
  public InterviewerTimeSlot getSlot(long id) {
    return timeSlotRepository.getReferenceById(id);
  }

  /**
   * Update slot by id.
   *
   * @param id      slot id
   * @param from    start time
   * @param to      end time
   * @param day     day of week
   * @param weekNum number of week
   */
  public InterviewerTimeSlot updateSlot(long id, String from, String to, String day, int weekNum) {
    // validate from, to, day, weekNum
    // check if current time is by end of Friday (00:00) of current week
    InterviewerTimeSlot slot = getSlot(id);
    slot.setFrom(LocalTime.parse(from));
    slot.setTo(LocalTime.parse(to));
    slot.setDayOfWeek(DayOfWeek.valueOf(day));
    slot.setWeekNum(weekNum);
    return timeSlotRepository.save(slot);
  }
}
