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
   * @param from start time
   * @param to   end time
   * @param day  day of week
   * @return slot
   */
  public InterviewerTimeSlot createSlot(String from, String to, String day, int weekNum) {
    return new InterviewerTimeSlot(from, to, day, weekNum);
  }

}
