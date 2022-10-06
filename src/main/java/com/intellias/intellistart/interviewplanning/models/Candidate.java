package com.intellias.intellistart.interviewplanning.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Candidate extends User {

  private final Map<Long, CandidateTimeSlot> timeSlots;
  private final Set<Booking> bookings;

  public Candidate(String email) {
    super(email, UserRole.CANDIDATE);
    timeSlots = new HashMap<>();
    bookings = new HashSet<>();
  }

  public void addSlot(CandidateTimeSlot slot) {
    //todo implements this
  }
  public void addBooking(Booking booking){
    bookings.add(booking);
    timeSlots.remove(booking.getId());
    //todo check this
  }

  public void editSlot(long slotID) {
    //todo implements this
  }

  public HashMap<Long, CandidateTimeSlot> getSlots() {
    return new HashMap<>(timeSlots);
  }
}
