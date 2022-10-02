package com.intellias.intellistart.interviewplanning.models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;

/**
 * Class for interviewer and candidate.
 */
@Data
@Entity
public class UserWithSlots extends User {

  @OneToMany()
  private List<TimeSlot> slots;

  public void addSlot(TimeSlot slot) {
    //TODO: validate
  }

  public void removeSlot(TimeSlot slot) {
    //TODO: validate
  }

  public void addBooking(Booking booking) {
    //TODO: validate
  }

}
