package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Class where was validated and corrected time from and to.
 */
@Getter
@Setter
public class Period {

  private LocalTime from;
  private LocalTime to;

  public Period(String from, String to) {
    this.from = LocalTime.parse(from);
    this.to = LocalTime.parse(to);
  }
}
