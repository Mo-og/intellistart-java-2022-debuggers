package com.intellias.intellistart.interviewplanning.models;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Candidate.
 */
@Entity
public class Candidate extends User {

  @OneToMany
  private Set<CandidateTimeSlot> timeSlots;
  @OneToMany
  private Set<Booking> bookings;

  /**
   * Constructor.
   *
   * @param email candidate email
   */
  public Candidate(String email) {
    super(email, UserRole.CANDIDATE);
  }

  public Candidate() {

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
    //todo implement this
  }

  public void editSlot(long slotId) {
    //todo implements this
  }

  public Set<CandidateTimeSlot> getSlots() {
    return new HashSet<>(timeSlots);
  }
}
