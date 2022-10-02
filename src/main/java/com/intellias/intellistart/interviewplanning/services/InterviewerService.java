package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import org.springframework.stereotype.Service;

@Service
public class InterviewerService {

  public InterviewerTimeSlot createSlot(String from, String to, String dayOfWeek) {
    return new InterviewerTimeSlot(InterviewerSlotForm.builder()
        .from(from)
        .to(to)
        .dayOfWeek(dayOfWeek)
        .build());
  }

}
