package com.intellias.intellistart.interviewplanning.models.interfaces;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public interface TimeSlot {

  LocalTime getFrom();

  LocalTime getTo();

  DayOfWeek getDayOfWeek();

  int getWeekNum();

  LocalDate getDate();

}
