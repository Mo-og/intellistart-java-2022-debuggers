package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interview Time slot.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class InterviewerTimeSlot {

  @ManyToOne
  @JsonIgnore
  User interviewer;
  @Id
  @SequenceGenerator(name = "interv_seq", sequenceName = "interviewer_slot_seq", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interv_seq")
  @Column(nullable = false)
  private Long id;
  @Column(name = "FROM_TIME")
  private LocalTime from;
  @Column(name = "TO_TIME")
  private LocalTime to;
  private DayOfWeek dayOfWeek;
  private int weekNum;

  /**
   * Constructor.
   *
   * @param from    start time
   * @param to      end time
   * @param day     day of week
   * @param weekNum week number
   */
  public InterviewerTimeSlot(String from, String to, String day, int weekNum) {
    this.from = LocalTime.parse(from);
    this.to = LocalTime.parse(to);
    this.weekNum = weekNum;
    setDayOfWeek(day);
  }

  @Transactional
  public void setInterviewer(User interviewer) {
    this.interviewer = interviewer;
  }

  @JsonGetter("dayOfWeek")
  public String getShortDayOfWeek() {
    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
  }

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }

  /**
   * Web request dayOfWeek format parser.
   *
   * @param dayOfWeek short form of day of week like 'Mon' for Monday
   */
  public void setDayOfWeek(String dayOfWeek) {
    if (dayOfWeek.length() == 3) {
      this.dayOfWeek = DayOfWeek.from(Utils.DAY_OF_WEEK_FORMATTER.parse(dayOfWeek));
    } else {
      this.dayOfWeek = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
    }
  }

  public void setDayOfWeek(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    InterviewerTimeSlot that = (InterviewerTimeSlot) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
