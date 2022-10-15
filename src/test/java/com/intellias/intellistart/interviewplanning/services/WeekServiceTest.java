package com.intellias.intellistart.interviewplanning.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class WeekServiceTest {

  @Test
  void testGetCurrentSameAsGetFromDateToday() {
    var a = WeekService.getCurrentWeekNum();
    var b = WeekService.getWeekNumByDate(LocalDate.now());
    log.debug(a + " == " + b);
    assertThat(a).isEqualTo(b);
  }

  @Test
  void testGetNextWeekForLastDateOfYearIsFirstWeekOfNextYear() {
    var date = LocalDate.of(2022, 12, 31);
    var a = WeekService.getWeekNumByDate(date);
    var b = WeekService.getWeekNumByDate(date.plusWeeks(1));
    log.debug(a + " != " + b);
    assertThat(a).isNotEqualTo(b);
  }

  @Test
  void testGetNextSameAsGetFromDateWeekLater() {
    var a = WeekService.getNextWeekNum();
    var b = WeekService.getWeekNumByDate(LocalDate.now().plusWeeks(1));
    log.debug(a + " == " + b);
    assertThat(a).isEqualTo(b);
  }

  @Test
  void testEndAndStartOfYearHandledCorrectly() {
    var a = WeekService.getWeekNumByDate(LocalDate.of(2022, 12, 31));
    var b = WeekService.getWeekNumByDate(LocalDate.of(2023, 1, 1));
    log.debug(a + " == " + b);
    assertThat(a).isEqualTo(b);

    a = WeekService.getWeekNumByDate(LocalDate.of(2019, 12, 31));
    b = WeekService.getWeekNumByDate(LocalDate.of(2020, 1, 1));
    log.debug(a + " == " + b);
    assertThat(a).isEqualTo(b);
  }
}
