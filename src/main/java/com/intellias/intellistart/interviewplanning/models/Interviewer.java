package com.intellias.intellistart.interviewplanning.models;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interviewer.
 */
@Entity
public class Interviewer extends User {

  @OneToMany
  private Set<InterviewerTimeSlot> timeSlots;
  @OneToMany
  private Set<Booking> bookings;

  /**
   * Constructor.
   *
   * @param email interviewer email
   */
  public Interviewer(String email) {
    super(email, UserRole.INTERVIEWER);
  }

  public Interviewer() {

  }

  @Transactional
  public void addSlot(InterviewerTimeSlot slot) {
    timeSlots.add(slot);
  }

  public void addBooking(Booking booking) {
    bookings.add(booking);
    //todo check this
  }

  public void editSlot(long slotId) {
    //todo implements this
  }

  public Set<InterviewerTimeSlot> getSlots() {
    return new HashSet<>(timeSlots);
  }
}
