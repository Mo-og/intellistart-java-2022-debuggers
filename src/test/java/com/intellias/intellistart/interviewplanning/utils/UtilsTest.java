package com.intellias.intellistart.interviewplanning.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UtilsTest {

  @Test
  void timeAsStringTest() {
    assertEquals("10:00", Utils.timeAsString(LocalTime.of(10, 0)));
    assertEquals("10:30", Utils.timeAsString(LocalTime.of(10, 30)));
    assertNotEquals("10:00", Utils.timeAsString(LocalTime.of(10, 30)));
  }

  @Test
  void getWeekNumByDateTest() {
    assertEquals(202001, Utils.getWeekNumByDate(LocalDate.of(2020, 1, 1)));
    assertEquals(202250, Utils.getWeekNumByDate(LocalDate.of(2022, 12, 12)));
    assertNotEquals(202001, Utils.getWeekNumByDate(LocalDate.of(2022, 12, 12)));
  }

}
