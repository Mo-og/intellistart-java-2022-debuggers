package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
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
  public InterviewerTimeSlot createSlot(String from, String to, String dayOfWeek) {
    return new InterviewerTimeSlot(InterviewerSlotForm.builder()
        .from(from)
        .to(to)
        .dayOfWeek(dayOfWeek)
        .build());
  }

}
