package com.intellias.intellistart.interviewplanning.models;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

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
