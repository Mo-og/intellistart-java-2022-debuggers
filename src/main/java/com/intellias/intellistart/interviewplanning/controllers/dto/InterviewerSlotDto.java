package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.models.interfaces.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InterviewerSlot dto.
 */
@Data
@NoArgsConstructor
public class InterviewerSlotDto implements InterviewerTimeSlot {

  private Long id;
  private Integer weekNum;
  private DayOfWeek dayOfWeek;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
  @JsonInclude(Include.NON_EMPTY)
  private List<BookingDto> bookings;

  @JsonGetter("from")
  public String getFromAsString() {
    return Utils.timeAsString(from);
  }

  @JsonGetter("to")
  public String getToAsString() {
    return Utils.timeAsString(to);
  }

  @JsonGetter("dayOfWeek")
  public String getShortDayOfWeek() {
    return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US);
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

  /**
   * Constructor.
   *
   * @param id        id
   * @param weekNum   weekNum
   * @param dayOfWeek dayOfWeek
   * @param from      from
   * @param to        to
   * @param bookings  bookings
   */
  public InterviewerSlotDto(Long id, int weekNum, String dayOfWeek, LocalTime from, LocalTime to,
      List<BookingDto> bookings) {
    this.id = id;
    this.weekNum = weekNum;
    setDayOfWeek(dayOfWeek);
    this.from = from;
    this.to = to;
    this.bookings = bookings;
  }

  /**
   * Constructor.
   *
   * @param id        id
   * @param weekNum   weekNum
   * @param dayOfWeek dayOfWeek
   * @param from      from
   * @param to        to
   */
  public InterviewerSlotDto(Long id, int weekNum, String dayOfWeek, LocalTime from,
      LocalTime to) {
    this.id = id;
    this.weekNum = weekNum;
    setDayOfWeek(dayOfWeek);
    this.from = from;
    this.to = to;
  }
}
