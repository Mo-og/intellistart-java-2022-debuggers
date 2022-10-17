package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
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
  @JsonIgnore
  private Set<CandidateTimeSlot> timeSlots;
  @OneToMany
  @JsonIgnore
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
    timeSlots.add(slot);
  }

  public void addBooking(Booking booking) {
    bookings.add(booking);
  }

  public Set<CandidateTimeSlot> getSlots() {
    return new HashSet<>(timeSlots);
  }
}