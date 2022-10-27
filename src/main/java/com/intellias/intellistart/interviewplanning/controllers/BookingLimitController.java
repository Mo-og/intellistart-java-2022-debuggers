package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
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
}