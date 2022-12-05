package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidInputException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Booking limit service.
 */
@Service
@RequiredArgsConstructor
public class BookingLimitService {

  private final BookingLimitRepository bookingLimitRepository;
  private final UserRepository userRepository;
  private final WeekService weekService;


  /**
   * Set bookings limit for interviewer per week.
   *
   * @param interviewerId       interviewer's id
   * @param bookingLimitRequest request DTO
   * @return Booking limit
   */
  public BookingLimit saveBookingLimit(Long interviewerId, BookingLimitDto bookingLimitRequest) {
    if (!userRepository.existsById(interviewerId)) {
      throw NotFoundException.interviewer(interviewerId);
    }
    if (bookingLimitRequest.getWeekNum() != weekService.getNextWeekNum()) {
      throw InvalidInputException.weekNum(bookingLimitRequest.getWeekNum());
    }
    if (bookingLimitRequest.getBookingLimit() <0) {
      throw InvalidInputException.bookingLimitValue(bookingLimitRequest.getBookingLimit());
    }
    BookingLimit bookingLimit = bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId,
            bookingLimitRequest.getWeekNum())
        .orElseGet(() -> new BookingLimit(interviewerId));
    bookingLimit.setWeekNum(bookingLimitRequest.getWeekNum());
    bookingLimit.setValue(bookingLimitRequest.getBookingLimit());
    return bookingLimitRepository.save(bookingLimit);
  }

  public List<BookingLimit> getBookingLimitsByWeekNum(Integer weekNum) {
    return bookingLimitRepository.findAllByWeekNum(weekNum);
  }

  /**
   * Receive BookingLimit of interviewer by weekNum.
   *
   * @param interviewerId interviewer's id
   * @param weekNum       week number
   * @return BookingLimit
   */
  public BookingLimit findBookingLimit(Long interviewerId, Integer weekNum) {
    if (!userRepository.existsById(interviewerId)) {
      throw NotFoundException.interviewer(interviewerId);
    }
    return bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId, weekNum)
        .orElseThrow(() -> NotFoundException.bookingLimit(interviewerId, weekNum));
  }
}