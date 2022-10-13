package com.intellias.intellistart.interviewplanning.services;

import ch.qos.logback.core.util.Loader;
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
   * CreateBook.
   *
   * @param candidateSlot   candidate slot
   * @param interviewerSlot interviewer Slot
   * @return booking
   */
  public Booking createBooking(CandidateTimeSlot candidateSlot,
      InterviewerTimeSlot interviewerSlot) {
    //Todo calculate possible time
    return bookingRepository.save(new Booking(candidateSlot, interviewerSlot));
  }

  public Booking createBooking(LocalTime from, LocalTime to, CandidateTimeSlot candidateSlot,
      InterviewerTimeSlot interviewerSlot) {
    //Todo calculate possible time
    return bookingRepository.save(new Booking(from, to, candidateSlot, interviewerSlot));
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
