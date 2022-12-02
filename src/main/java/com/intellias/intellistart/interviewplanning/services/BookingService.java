package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.BookingMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Booking service.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final BookingLimitRepository bookingLimitRepository;
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
    Long interviewerSlotId = bookingDto.getInterviewerSlotId();
    InterviewerTimeSlot interviewerSlot = interviewerTimeSlotRepository.findById(interviewerSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(interviewerSlotId));

    checkBookingLimit(interviewerSlot);

    Long candidateSlotId = bookingDto.getCandidateSlotId();
    CandidateTimeSlot candidateSlot = candidateTimeSlotRepository.findById(candidateSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(candidateSlotId));

    Booking booking = BookingMapper.mapToEntity(bookingDto, interviewerSlot, candidateSlot);

    return BookingMapper.mapToDto(bookingRepository.save(booking));
  }

  /**
   * Checks or the interviewer booking limit allows to create a new booking.
   *
   * @param interviewerSlot interviewer time slot
   */
  private void checkBookingLimit(InterviewerTimeSlot interviewerSlot) {
    Long interviewerId = interviewerSlot.getInterviewer().getId();
    int weekNum = interviewerSlot.getWeekNum();

    Optional<BookingLimit> bookingLimit = bookingLimitRepository
        .findByInterviewerIdAndWeekNum(interviewerId, weekNum);

    if (bookingLimit.isEmpty()) {
      bookingLimit = setPreviousBookingLimit(interviewerId, weekNum);
    }

    if (bookingLimit.isPresent()) {
      int bookingWeekCount = getInterviewerWeekBookingCount(interviewerId, weekNum);
      int bookingWeekLimitCount = bookingLimit.get().getBookingLimit();

      if (bookingWeekLimitCount < bookingWeekCount + 1) {
        throw InvalidInputException.exceedsBookingLimit(bookingWeekLimitCount);
      }
    }
  }

  private Optional<BookingLimit> setPreviousBookingLimit(Long interviewerId, int weekNum) {
    List<BookingLimit> bookingLimits = bookingLimitRepository
        .findByInterviewerIdAndWeekNumLessThan(interviewerId, weekNum);

    bookingLimits.sort(Comparator.comparing(BookingLimit::getWeekNum).reversed());
    Optional<BookingLimit> bookingLimit = bookingLimits.stream().findFirst();

    if (bookingLimit.isPresent()) {
      bookingLimit.get().setWeekNum(weekNum);
      bookingLimitRepository.save(bookingLimit.get());
    }

    return bookingLimit;
  }

  private int getInterviewerWeekBookingCount(Long interviewerId, int weekNum) {
    List<InterviewerTimeSlot> weekInterviewerSlots = interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(interviewerId, weekNum);

    List<Booking> weekInterviewerBookings = new ArrayList<>();
    for (InterviewerTimeSlot slot : weekInterviewerSlots) {
      weekInterviewerBookings.addAll(bookingRepository.findByInterviewerSlot(slot));
    }

    return weekInterviewerBookings.size();
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
