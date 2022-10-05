package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;

/**
 * Candidate service.
 */
public class CandidateService {

  /**
   * Crete slot for candidate.
   *
   * @param from start time
   * @param to   end time
   * @param date date
   * @return slot
   */
  public static CandidateTimeSlot createSlot(String from, String to, String date) {
    return new CandidateTimeSlot(date, from, to);
  }
}
