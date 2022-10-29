package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekEditException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.models.dto.BookingLimitResponse;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Booking limit service.
 */
@Service
public class BookingLimitService {

  private final BookingLimitRepository bookingLimitRepository;
  private final UserRepository userRepository;

  @Autowired
  public BookingLimitService(BookingLimitRepository bookingLimitsRepository,
      UserRepository userRepository) {
    this.bookingLimitRepository = bookingLimitsRepository;
    this.userRepository = userRepository;
  }

  /**
   * set bookings limit for interviewer per week.
   *
   * @param limit         bookings limit
   * @param interviewerId interviewer's id
   * @return Booking limit
   */
  public BookingLimit setBookingLimit(Integer limit, Long interviewerId, Integer weekNum) {
    if (!userRepository.existsById(interviewerId)) {
      throw new UserNotFoundException(interviewerId + "");
    }
    if (weekNum != WeekService.getNextWeekNum()) {
      throw new WeekEditException(weekNum, interviewerId);
    }
    BookingLimit bookingLimit = bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId,
        weekNum);
    if (bookingLimit != null) {
      bookingLimit.setBookingLimit(limit);
    } else {
      bookingLimit = new BookingLimit(interviewerId, weekNum, limit);
    }

    return bookingLimitRepository.save(bookingLimit);
  }

  public List<BookingLimit> getWeekBookingLimits(Integer weekNum) {
    return bookingLimitRepository.findAllByWeekNum(weekNum);
  }

  /**
   * Receive BookingLimit of interviewer by weekNum.
   *
   * @param interviewerId interviewer's id
   * @param weekNum       week number
   * @return BookingLimit
   */
  public BookingLimit getBookingLimit(Long interviewerId, Integer weekNum) {
    if (!userRepository.existsById(interviewerId)) {
      throw new UserNotFoundException(interviewerId + "");
    }
    return bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId, weekNum);
  }

  public BookingLimitResponse getUserWeekBookingLimit(Long interviewerId, Integer weekNum) {
    BookingLimit bookingLimit = getBookingLimit(interviewerId, weekNum);
    return new BookingLimitResponse(bookingLimit.getBookingLimit());
  }

}