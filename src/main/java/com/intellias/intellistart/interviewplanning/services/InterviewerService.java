package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotForm;
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
  public InterviewerTimeSlot createSlot(String from, String to, String dayOfWeek, int weekNum) {
    return new InterviewerTimeSlot(InterviewerTimeSlotForm.builder()
      .from(from)
      .to(to)
      .dayOfWeek(dayOfWeek)
      .weekNum(weekNum)
      .build());
  }

}
