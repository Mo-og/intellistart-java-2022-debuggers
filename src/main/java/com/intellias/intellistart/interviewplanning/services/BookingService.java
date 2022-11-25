package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Booking service.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private final CandidateTimeSlotRepository candidateTimeSlotRepository;

  /**
   * Creates new booking.
   *
   * @param bookingDto object with data to create
   * @return object with created booking data
   * @throws NotFoundException if slot with the specified id is not found
   */
  public BookingDto createBooking(BookingDto bookingDto) {
    //Todo calculate possible time
    //Todo check booking limit
    Long interviewerSlotId = bookingDto.getInterviewerSlotId();
    InterviewerTimeSlot interviewerSlot = interviewerTimeSlotRepository.findById(interviewerSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(interviewerSlotId));

    Long candidateSlotId = bookingDto.getCandidateSlotId();
    CandidateTimeSlot candidateSlot = candidateTimeSlotRepository.findById(candidateSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(candidateSlotId));

    Booking booking = BookingMapper.mapToEntity(bookingDto, interviewerSlot, candidateSlot);

    return BookingMapper.mapToDto(bookingRepository.save(booking));
  }

  /**
   * Updates existing booking.
   *
   * @param id         booking id
   * @param bookingDto object with data to update
   * @return booking with new parameters
   * @throws NotFoundException if booking with the specified id is not found
   */
  public BookingDto updateBooking(Long id, BookingDto bookingDto) {
    //Todo validate new booking info
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> NotFoundException.booking(id));
    booking.setFrom(bookingDto.getFrom());
    booking.setTo(bookingDto.getTo());
    booking.setSubject(bookingDto.getSubject());
    booking.setDescription(bookingDto.getDescription());
    return BookingMapper.mapToDto(bookingRepository.save(booking));
  }

  /**
   * Deletes booking by id.
   *
   * @param id booking id
   * @return deleted booking
   * @throws NotFoundException if booking with the specified id is not found
   */
  public BookingDto deleteBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> NotFoundException.booking(id));
    bookingRepository.delete(booking);
    return BookingMapper.mapToDto(booking);
  }
}
