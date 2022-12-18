package com.intellias.intellistart.interviewplanning.models.interfaces;

import java.time.LocalDate;

/**
 * CandidateTimeSlot interface.
 */
public interface CandidateTimeSlot extends Period {

  LocalDate getDate();

}
