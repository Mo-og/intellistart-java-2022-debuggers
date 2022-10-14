package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Booking service.
 */
@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  @Autowired
  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  /**
   * Create booking.
   *
   * @param from            start time
   * @param to              end time
   * @param candidateSlot   candidate slot
   * @param interviewerSlot interviewer slot
   * @param subject         subject
   * @param description     description of this booking
   * @return created booking
   */
  public Booking createBooking(LocalTime from, LocalTime to, CandidateTimeSlot candidateSlot,
      InterviewerTimeSlot interviewerSlot, String subject, String description) {
    //Todo calculate possible time
    return bookingRepository.save(
        new Booking(from, to, candidateSlot, interviewerSlot, subject, description));
  }

  /**
   * Update existing booking.
   *
   * @param id booking id
   * @param newBooking object with data to update
   * @return booking with new parameters
   */
  public Booking updateBooking(long id, Booking newBooking) {
    Booking booking = bookingRepository.getReferenceById(id);
    booking.setFrom(newBooking.getFrom());
    booking.setTo(newBooking.getTo());
    booking.setSubject(newBooking.getSubject());
    booking.setDescription(newBooking.getDescription());
    return booking;
  }

  public Booking saveBooking(Booking booking) {
    return bookingRepository.save(booking);
  }

  public Booking getBooking(Long id) {
    return bookingRepository.getReferenceById(id);
  }

  public void removeBooking(Long id) {
    bookingRepository.deleteById(id);
  }


}
