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

  /**
   * Add booking to set of bookings, and remove from timeslots list.
   *
   * @param booking booking
   */
  public void addBooking(Booking booking) {
    bookings.add(booking);
  }

  /**
   * Edits time slot.
   *
   * @param slotId slot id
   * @param from   start time
   * @param to     end time
   * @param date   date
   * @return boolean as result of operation
   */
  public boolean editSlot(long slotId, String from, String to, String date) {
    Period period = new Period(from, to);
    for (CandidateTimeSlot timeSlot : timeSlots) {
      if (timeSlot.getId() == slotId) {
        timeSlot.setFrom(period.getFrom());
        timeSlot.setTo(period.getTo());
        timeSlot.setDate(LocalDate.parse(date));
        return true;
      }
    }
    return false;
  }

  @JsonIgnore
  public Set<CandidateTimeSlot> getSlots() {
    return new HashSet<>(timeSlots);
  }
}