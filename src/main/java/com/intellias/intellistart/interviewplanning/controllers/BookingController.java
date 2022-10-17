package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
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
  private final InterviewerService interviewerService;
  private final CandidateService candidateService;

  /**
   * Constructor.
   *
   * @param bookingService     booking service
   * @param interviewerService interviewer service
   * @param candidateService   candidate service
   */
  @Autowired
  public BookingController(BookingService bookingService,
      InterviewerService interviewerService,
      CandidateService candidateService) {
    this.bookingService = bookingService;
    this.interviewerService = interviewerService;
    this.candidateService = candidateService;
  }

  @PostMapping("/bookings")
  public Booking createBooking(@RequestBody Booking booking) {
    return bookingService.createBooking(booking);
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
  public boolean deleteBooking(@PathVariable long bookingId) {
    bookingService.removeBooking(bookingId);
    return true;
  }

}