package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Booking controller.
 */
@RestController
public class BookingController {

  private final BookingService bookingService;

  /**
   * Constructor.
   *
   * @param bookingService booking service
   */
  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping("/bookings")
  public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }

  /**
   * Update booking.
   *
   * @param bookingId id of the booking
   * @return booking
   */
  @PostMapping("/bookings/{bookingId}")
  public Booking updateBooking(@PathVariable long bookingId, @RequestBody Booking booking) {
    return bookingService.updateBooking(bookingId, booking);
  }

  @DeleteMapping("/bookings/{bookingId}")
  public BookingDto deleteBooking(@PathVariable long bookingId) {
    return bookingService.removeBooking(bookingId);
  }
}