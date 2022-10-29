package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.dto.BookingLimitResponse;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Booking limit controller.
 */
@RestController
public class BookingLimitController {

  private final BookingLimitService bookingLimitService;

  public BookingLimitController(BookingLimitService bookingLimitService) {
    this.bookingLimitService = bookingLimitService;
  }


  @PostMapping("/interviewers/current/bookingLimits")
  public BookingLimit setBookingLimit(
      @RequestParam Integer bookingLimit,
      @RequestParam Long interviewerId,
      @RequestParam Integer weekNum) {
    return bookingLimitService.setBookingLimit(bookingLimit, interviewerId, weekNum);
  }

  @GetMapping("/interviewers/bookingLimits/week")
  public List<BookingLimit> getWeekBookingLimits(@RequestParam Integer weekNum) {
    return bookingLimitService.getWeekBookingLimits(weekNum);
  }

  @GetMapping("/interviewers/bookingLimits/week/user")
  public BookingLimit getBookingLimit(@RequestParam Long interviewerId, Integer weekNum) {
    return bookingLimitService.getBookingLimit(interviewerId, weekNum);
  }

  @GetMapping("/interviewers/bookingLimits/week/user/limit")
  public BookingLimitResponse getUserWeekBookingLimit(@RequestParam Long interviewerId,
      Integer weekNum) {
    return bookingLimitService.getUserWeekBookingLimit(interviewerId, weekNum);
  }
}