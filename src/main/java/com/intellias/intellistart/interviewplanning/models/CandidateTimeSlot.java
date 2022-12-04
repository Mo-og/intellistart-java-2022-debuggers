package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.models.interfaces.TimeSlot;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Candidate time slot.
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class CandidateTimeSlot implements TimeSlot {

  @Id
  @SequenceGenerator(name = "cnd_seq", sequenceName = "candidate_slot_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cnd_seq")
  @Column(nullable = false)
  private Long id;
  private String email;
  @Column(name = "date_value")
  private LocalDate date;
  @Column(name = "from_time")
  private LocalTime from;
  @Column(name = "to_time")
  private LocalTime to;

  /**
   * Constructor.
   *
   * @param email candidate email
   * @param date  date
   * @param from  start time
   * @param to    end time
   */
  public CandidateTimeSlot(String email, String date, String from, String to) {
    this.email = email;
    this.date = LocalDate.parse(date);
    this.from = LocalTime.parse(from);
    this.to = LocalTime.parse(to);
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
  public DayOfWeek getDayOfWeek() {
    return date.getDayOfWeek();
  }

  @Override
  public int getWeekNum() {
    return Utils.getWeekNumByDate(date);
  }

}
