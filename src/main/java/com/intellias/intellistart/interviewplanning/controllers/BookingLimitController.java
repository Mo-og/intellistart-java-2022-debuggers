package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.dto.BookingLimitRequest;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


  @PostMapping("/interviewers/{interviewerId}/bookingLimits")
  public BookingLimit setBookingLimit(@PathVariable Long interviewerId,
      @RequestBody BookingLimitRequest request) {
    return bookingLimitService.setBookingLimit(interviewerId, request);
  }

  @GetMapping("/interviewers/bookingLimits/{weekNum}")
  public List<BookingLimit> getWeekBookingLimits(@PathVariable Integer weekNum) {
    return bookingLimitService.getWeekBookingLimits(weekNum);
  }

  @GetMapping("/interviewers/{interviewerId}/bookingLimits/{weekNum}")
  public BookingLimit getBookingLimit(@PathVariable Long interviewerId,
      @PathVariable Integer weekNum) {
    return bookingLimitService.getBookingLimit(interviewerId, weekNum);
  }
}