package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekEditException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
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
      bookingLimit = new BookingLimit(interviewerId, weekNum, 0);
    }

    return bookingLimitRepository.save(bookingLimit);
  }
}