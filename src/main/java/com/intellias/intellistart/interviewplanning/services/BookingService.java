package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.TimeSlotNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Booking service.
 */
@Service
public class BookingService {

  private final BookingRepository bookingRepository;
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;

  /**
   * Constructor.
   *
   * @param bookingRepository             booking repository
   * @param interviewerTimeSlotRepository interviewer time slot repository
   * @param candidateTimeSlotRepository   candidate time slot repository
   */
  @Autowired
  public BookingService(BookingRepository bookingRepository,
      InterviewerTimeSlotRepository interviewerTimeSlotRepository,
      CandidateTimeSlotRepository candidateTimeSlotRepository) {
    this.bookingRepository = bookingRepository;
    this.interviewerTimeSlotRepository = interviewerTimeSlotRepository;
    this.candidateTimeSlotRepository = candidateTimeSlotRepository;
  }

  /**
   * Create new booking.
   *
   * @param bookingDto object with data to create
   * @return created booking
   */
  public BookingDto createBooking(BookingDto bookingDto) {
    //Todo calculate possible time

    Long interviewerSlotId = bookingDto.getInterviewerSlotId();
    InterviewerTimeSlot interviewerSlot = interviewerTimeSlotRepository.findById(interviewerSlotId)
        .orElseThrow(() -> new TimeSlotNotFoundException(interviewerSlotId));

    Long candidateSlotId = bookingDto.getCandidateSlotId();
    CandidateTimeSlot candidateSlot = candidateTimeSlotRepository.findById(candidateSlotId)
        .orElseThrow(() -> new TimeSlotNotFoundException(candidateSlotId));

    Booking booking = BookingMapper.mapToBookingEntity(bookingDto, interviewerSlot, candidateSlot);

    return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
  }

  /**
   * Update existing booking.
   *
   * @param id         booking id
   * @param newBooking object with data to update
   * @return booking with new parameters
   */
  public Booking updateBooking(Long id, Booking newBooking) {
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
