package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Interviewer.
 */
@Entity
@Table(name = "INTERVIEWER")
public class Interviewer extends User {


  @JsonIgnore
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

  /**
   * To be replaced by addInterviewer() in booking class.
   *
   * @param booking param
   */
  public void addBooking(Booking booking) {
    if (booking == null) {
      bookings = new HashSet<>();
    }
    bookings.add(booking);
    //todo check this
  }

  public void editSlot(long slotId) {
    //todo implements this
  }

}
