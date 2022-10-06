package com.intellias.intellistart.interviewplanning.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interviewer.
 */
public class Interviewer extends User {

  private final Map<Long, InterviewerTimeSlot> timeSlots;
  private final Set<Booking> bookings;

  /**
   * Constructor.
   *
   * @param email interviewer email
   */
  public Interviewer(String email) {
    super(email, UserRole.INTERVIEWER);
    timeSlots = new HashMap<>();
    bookings = new HashSet<>();
  }

  public void addSlot(InterviewerTimeSlot slot) {
    //todo implements this
  }

  public void addBooking(Booking booking) {
    bookings.add(booking);
    //todo check this
  }

  public void editSlot(long slotId) {
    //todo implements this
  }

  public HashMap<Long, InterviewerTimeSlot> getSlots() {
    return new HashMap<>(timeSlots);
  }
}
