package com.intellias.intellistart.interviewplanning.models.interfaces;

import java.time.DayOfWeek;

/**
 * InterviewerTimeSlot interface.
 */
public interface InterviewerTimeSlot extends Period {

  DayOfWeek getDayOfWeek();

  Integer getWeekNum();

}
