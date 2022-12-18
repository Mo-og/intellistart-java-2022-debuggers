package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.utils.mappers.BookingMapper;
import com.intellias.intellistart.interviewplanning.validators.BookingValidator;
import com.intellias.intellistart.interviewplanning.validators.PeriodValidator;
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
  private final BookingValidator bookingValidator;

  /**
   * Creates new booking.
   *
   * @param bookingDto object with data to create
   * @return object with created booking data
   * @throws NotFoundException if slot with the specified id is not found
   */
  public BookingDto createBooking(BookingDto bookingDto) {
    PeriodValidator.validate(bookingDto);
    bookingValidator.validateTextFieldsLength(bookingDto);

    Long interviewerSlotId = bookingDto.getInterviewerSlotId();
    InterviewerTimeSlotImpl interviewerSlot = interviewerTimeSlotRepository.findById(interviewerSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(interviewerSlotId));

    checkFitsBookingLimit(interviewerSlot);

    Long candidateSlotId = bookingDto.getCandidateSlotId();
    CandidateTimeSlotImpl candidateSlot = candidateTimeSlotRepository.findById(candidateSlotId)
        .orElseThrow(() -> NotFoundException.timeSlot(candidateSlotId));

    BookingImpl booking = BookingMapper.mapToEntity(bookingDto, interviewerSlot, candidateSlot);
    bookingValidator.validateSlotOverlapping(booking, booking.getInterviewerSlot(), booking.getCandidateSlot());
    return BookingMapper.mapToDto(bookingRepository.save(booking));
  }

  /**
   * Checks if the interviewer booking limit allows to create a new booking.
   *
   * @param interviewerSlot interviewer time slot
   */
  private void checkFitsBookingLimit(InterviewerTimeSlotImpl interviewerSlot) {
    Long interviewerId = interviewerSlot.getInterviewer().getId();
    int weekNum = interviewerSlot.getWeekNum();

    BookingLimit bookingLimit = getOrCreateBookingLimit(interviewerId, weekNum);
    int maxBookings = bookingLimit.getValue();
    int currentBookingsCount = getBookingsCountForWeek(interviewerId, weekNum);

    if (maxBookings < currentBookingsCount + 1) {
      throw InvalidInputException.exceedsBookingLimit(maxBookings);
    }
  }

  private BookingLimit getOrCreateBookingLimit(Long interviewerId, int weekNum) {
    Optional<BookingLimit> bookingLimit = bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId, weekNum);
    if (bookingLimit.isEmpty()) {
      return saveAndGetPreviousBookingLimit(interviewerId, weekNum);
    }
    return bookingLimit.get();
  }

  private BookingLimit saveAndGetPreviousBookingLimit(Long interviewerId, int weekNum) {
    List<BookingLimit> bookingLimits = bookingLimitRepository.findByInterviewerIdAndWeekNumLessThan(interviewerId,
        weekNum);
    if (bookingLimits.isEmpty()) {
      return bookingLimitRepository.save(new BookingLimit(interviewerId, weekNum, Integer.MAX_VALUE));
    }

    bookingLimits.sort(Comparator.comparing(BookingLimit::getWeekNum).reversed());
    BookingLimit bookingLimit = bookingLimits.get(0);
    bookingLimit.setWeekNum(weekNum);
    return bookingLimitRepository.save(bookingLimit);
  }

  private int getBookingsCountForWeek(Long interviewerId, int weekNum) {
    List<InterviewerTimeSlotImpl> weekInterviewerSlots =
        interviewerTimeSlotRepository.findByInterviewerIdAndWeekNum(interviewerId, weekNum);

    List<BookingImpl> weekInterviewerBookings = new ArrayList<>();
    for (InterviewerTimeSlotImpl slot : weekInterviewerSlots) {
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
    PeriodValidator.validate(bookingDto);
    bookingValidator.validateTextFieldsLength(bookingDto);

    BookingImpl booking = bookingRepository.findById(id)
        .orElseThrow(() -> NotFoundException.booking(id));

    bookingValidator.validateSlotOverlapping(booking, booking.getInterviewerSlot(), booking.getCandidateSlot());

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
    BookingImpl booking = bookingRepository.findById(id)
        .orElseThrow(() -> NotFoundException.booking(id));
    bookingRepository.delete(booking);
    return BookingMapper.mapToDto(booking);
  }

}
