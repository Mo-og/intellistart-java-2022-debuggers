package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.Booking.BookingStatus;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.BookingService;
import java.time.LocalTime;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  /**
   * Create booking for candidate and interviewer.
   *
   * @param interviewerTimeSlotId interviewer time slot id
   * @param candidateTimeSlotId candidate time slot id
   * @param from start time
   * @param to end time
   * @param subject subject of discussion
   * @param description description of the booking
   * @return booking //todo check this
   */
  @PostMapping("/bookings")
  public boolean createBooking(
      @RequestParam("interviewerTimeSlotId") long interviewerTimeSlotId,
      @RequestParam("candidateTimeSlotId") long candidateTimeSlotId,
      @RequestParam("from") LocalTime from,
      @RequestParam("to") LocalTime to,
      @RequestParam("subject") String subject,
      @RequestParam("description") String description) {

    //todo find timeslots of candidate and interviewer for creating booking
    CandidateTimeSlot candidateTimeSlot = new CandidateTimeSlot();
    InterviewerTimeSlot interviewerTimeSlot = new InterviewerTimeSlot();


    bookingService.createBooking(from, to, candidateTimeSlot, interviewerTimeSlot);
    return true;
  }

  /**
   * Update booking.
   *
   * @param bookingId id of the booking
   * @return booking  //todo real booking or true?
   */
  @PostMapping("/bookings/{bookingId}")
  public boolean updateBooking(@PathVariable long bookingId) {
    Booking booking = bookingService.getBooking(bookingId);
    booking.setFrom(LocalTime.now());
    booking.setTo(LocalTime.now());
    booking.setStatus(BookingStatus.BOOKED);
    return true;
  }

  @DeleteMapping("/bookings/{bookingId}")
  public boolean deleteBooking(@PathVariable long bookingId) {
    bookingService.removeBooking(bookingId);
    return true;
  }

}