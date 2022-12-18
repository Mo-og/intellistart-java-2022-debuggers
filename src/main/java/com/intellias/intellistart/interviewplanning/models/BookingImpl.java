package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellias.intellistart.interviewplanning.models.interfaces.Booking;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * Booking.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "booking")
public class BookingImpl implements Booking {

  @Id
  @SequenceGenerator(name = "booking_seq", sequenceName = "booking_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "booking_seq")
  @Column(nullable = false)
  private Long id;
  @Column(name = "from_time")
  private LocalTime from;
  @Column(name = "to_time")
  private LocalTime to;
  private String subject;
  private String description;
  @ManyToOne
  @JsonIgnore
  private CandidateTimeSlotImpl candidateSlot;
  @ManyToOne
  @JsonIgnore
  private InterviewerTimeSlotImpl interviewerSlot;

  /**
   * Constructor.
   *
   * @param from            start time
   * @param to              end time
   * @param candidateSlot   candidate slot
   * @param interviewerSlot interviewer slot
   * @param subject         subject
   * @param description     description of this booking
   */
  public BookingImpl(LocalTime from, LocalTime to, CandidateTimeSlotImpl candidateSlot,
      InterviewerTimeSlotImpl interviewerSlot, String subject, String description) {
    this.from = from;
    this.to = to;
    this.candidateSlot = candidateSlot;
    this.interviewerSlot = interviewerSlot;
    this.subject = subject;
    this.description = description;
  }

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BookingImpl booking = (BookingImpl) o;
    return id != null && Objects.equals(id, booking.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
