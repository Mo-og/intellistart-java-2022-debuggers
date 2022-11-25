package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for managing bookings.
 */
@RestController
@RequiredArgsConstructor
public class BookingController {

  private final BookingService bookingService;

  /**
   * Endpoint to create booking.
   *
   * @param bookingDto booking information
   * @return created booking
   */
  @PostMapping("/bookings")
  public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }

  /**
   * Endpoint to update booking.
   *
   * @param bookingId  id of the booking
   * @param bookingDto booking information
   * @return updated booking
   */
  @PostMapping("/bookings/{bookingId}")
  public BookingDto updateBooking(@PathVariable long bookingId,
      @RequestBody BookingDto bookingDto) {
    return bookingService.updateBooking(bookingId, bookingDto);
  }

  /**
   * Endpoint to delete booking.
   *
   * @param bookingId id of the booking
   * @return deleted booking
   */
  @DeleteMapping("/bookings/{bookingId}")
  public BookingDto deleteBooking(@PathVariable long bookingId) {
    return bookingService.deleteBooking(bookingId);
  }
}
