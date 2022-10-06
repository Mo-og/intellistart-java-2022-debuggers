package com.intellias.intellistart.interviewplanning.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Candidate.
 */
public class Candidate extends User {

  private final Map<Long, CandidateTimeSlot> timeSlots;
  private final Set<Booking> bookings;

  /**
   * Constructor.
   *
   * @param email candidate email
   */
  public Candidate(String email) {
    super(email, UserRole.CANDIDATE);
    timeSlots = new HashMap<>();
    bookings = new HashSet<>();
  }

  public void addSlot(CandidateTimeSlot slot) {
    //todo implements this
  }

  /**
   * Add booking to set of bookings,
   * and remove from timeslots list.
   *
   * @param booking booking
   */
  public void addBooking(Booking booking) {
    bookings.add(booking);
    timeSlots.remove(booking.getId());
    //todo check this
  }

  public void editSlot(long slotId) {
    //todo implements this
  }

  public HashMap<Long, CandidateTimeSlot> getSlots() {
    return new HashMap<>(timeSlots);
  }
}
