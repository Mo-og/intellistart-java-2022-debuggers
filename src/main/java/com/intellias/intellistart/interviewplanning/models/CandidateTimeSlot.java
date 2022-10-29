package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellias.intellistart.interviewplanning.Utils;
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
import java.time.LocalDate;
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
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Candidate time slot.
 */
@Entity
@Getter
@Setter
@ToString
public class CandidateTimeSlot {
  @ManyToOne
  @JsonIgnore
  User candidate;

  @Id
  @SequenceGenerator(name = "cnd_seq", sequenceName = "candidate_slot_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnd_seq")
  @Column(nullable = false)
  private Long id;
  @Column(name = "from_time")
  private LocalTime from;
  @Column(name = "to_time")
  private LocalTime to;
  private LocalDate date;

  /**
   * Constructor.
   *
   * @param date date
   * @param from start time
   * @param to   end time
   */
  public CandidateTimeSlot(String date, String from, String to) {
    PeriodValidator.validate(from, to);
    this.date = LocalDate.parse(date);
    this.from = LocalTime.parse(from);
    this.to = LocalTime.parse(to);
  }

  public CandidateTimeSlot() {
  }

  @Transactional
  public void setCandidate(User candidate) {
    this.candidate = candidate;
  }

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }

  @JsonGetter("date")
  public String getDateAsString() {
    return date.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    CandidateTimeSlot that = (CandidateTimeSlot) o;
    if (id != null) {
      return Objects.equals(id, that.id);
    }
    return Objects.equals(from, that.from)
        && Objects.equals(to, that.to)
        && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
