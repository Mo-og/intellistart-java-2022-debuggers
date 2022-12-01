package com.intellias.intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * InterviewerSlot dto.
 */
@Data
@NoArgsConstructor
public class InterviewerSlotDto {

  private Long id;
  private int weekNum;
  private DayOfWeek dayOfWeek;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
  @JsonInclude(Include.NON_EMPTY)
  private List<BookingDto> bookings;

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

  /**
   * Constructor.
   *
   * @param from    start time
   * @param to      end time
   * @param day     day of week
   * @param weekNum week number
   */
  public InterviewerSlotDto(String from, String to, String day, int weekNum) {
    this.from = LocalTime.parse(from);
    this.to = LocalTime.parse(to);
    this.weekNum = weekNum;
    setDayOfWeek(day);
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
}
