package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.TimeSlot;
import com.intellias.intellistart.interviewplanning.models.TimeSlotForm;
import org.springframework.stereotype.Service;

/**
 * Interview service.
 */
@Service
public class InterviewerService {

  /**
   * crete slot for interview.
   *
   * @param from      start time
   * @param to        end time
   * @param dayOfWeek day
   * @return slot
   */
  public TimeSlot createSlot(String from, String to, String dayOfWeek, int weekNum) {
    return new TimeSlot(TimeSlotForm.builder()
        .from(from)
        .to(to)
        .dayOfWeek(dayOfWeek)
        .weekNum(weekNum)
        .build());
  }


}
