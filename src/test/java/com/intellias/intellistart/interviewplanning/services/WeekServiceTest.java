package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class WeekServiceTest {

  WeekService weekService = new WeekServiceImp();

  @Test
  void testGetCurrentSameAsGetFromDateToday() {
    var a = weekService.getCurrentWeekNum();
    var b = weekService.getWeekNumByDate(weekService.getCurrentDate());
    log.debug(a + " == " + b);
    assertEquals(a, b);
  }

  @Test
  void testGetNextWeekForLastDateOfYearIsFirstWeekOfNextYear() {
    var date = LocalDate.of(2022, 12, 31);
    var a = weekService.getWeekNumByDate(date);
    var b = weekService.getWeekNumByDate(date.plusWeeks(1));
    log.debug(a + " != " + b);
    assertNotEquals(a, b);
  }

  @Test
  void testGetNextSameAsGetFromDateWeekLater() {
    var a = weekService.getNextWeekNum();
    var b = weekService.getWeekNumByDate(weekService.getCurrentDate().plusWeeks(1));
    log.debug(a + " == " + b);
    assertEquals(a, b);
  }

  @Test
  void testEndAndStartOfYearHandledCorrectly() {
    var a = weekService.getWeekNumByDate(LocalDate.of(2022, 12, 31));
    var b = weekService.getWeekNumByDate(LocalDate.of(2023, 1, 1));
    log.debug(a + " == " + b);
    assertEquals(a, b);

    a = weekService.getWeekNumByDate(LocalDate.of(2019, 12, 31));
    b = weekService.getWeekNumByDate(LocalDate.of(2020, 1, 1));
    log.debug(a + " == " + b);
    assertEquals(a, b);
  }

  @Test
  void testGetCurrentDateTime() {
    var past = ZonedDateTime.now(WeekService.ZONE_ID).toLocalDateTime().minusMinutes(5);
    var currentDateTime = weekService.getCurrentDateTime();
    var future = ZonedDateTime.now(WeekService.ZONE_ID).toLocalDateTime().plusMinutes(5);

    assertTrue(currentDateTime.isAfter(past));
    assertTrue(currentDateTime.isBefore(future));
  }
}
