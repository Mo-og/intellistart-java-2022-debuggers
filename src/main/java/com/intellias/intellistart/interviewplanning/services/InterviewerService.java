package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Interview service.
 */
@Service
public class InterviewerService {

  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final InterviewerRepository interviewerRepository;

  /**
   * Constructor.
   *
   * @param interviewerTimeSlotRepository time slot repository bean
   * @param interviewerRepository         interviewer repository bean
   */
  @Autowired
  public InterviewerService(InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      InterviewerRepository interviewerRepository) {
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.interviewerRepository = interviewerRepository;
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
    return interviewerTimeSlotRepository.save(new InterviewerTimeSlot(from, to, day, weekNum));
  }

  /**
   * Get slot by id.
   *
   * @param id slot id
   * @return slotById
   */
  public InterviewerTimeSlot getSlot(long id) {
    return interviewerTimeSlotRepository.getReferenceById(id);
  }

  /**
   * Provides time slots for given user for current week and onwards.
   *
   * @param interviewerId id of interviewer to get slots from
   * @return time slots of requested interviewer for current week and future weeks
   */
  public Set<InterviewerTimeSlot> getRelevantInterviewerSlots(Long interviewerId) {
    Interviewer interviewer = interviewerRepository.getReferenceById(interviewerId);
    if (interviewer == null) {
      //todo make custom exception
      throw new NoSuchElementException("No user found by given id");
    }
    //todo get instead filtered slots from database
    return interviewer.getSlots().stream()
        .filter(interviewerTimeSlot ->
            interviewerTimeSlot.getWeekNum() >= UtilService.getCurrentWeekNum())
        .collect(Collectors.toSet());
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
    return interviewerTimeSlotRepository.save(slot);
  }
}
