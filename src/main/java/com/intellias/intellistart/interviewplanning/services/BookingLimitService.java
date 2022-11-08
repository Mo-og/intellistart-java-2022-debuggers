package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.exceptions.BadRequestException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
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
   * Set bookings limit for interviewer per week.
   *
   * @param interviewerId       interviewer's id
   * @param bookingLimitRequest request DTO
   * @return Booking limit
   */
  public BookingLimit saveBookingLimit(
      Long interviewerId,
      BookingLimitDto bookingLimitRequest) {
    if (!userRepository.existsById(interviewerId)) {
      throw NotFoundException.interviewerNotFound(interviewerId);
    }
    if (bookingLimitRequest.getWeekNum() != WeekService.getNextWeekNum()) {
      throw BadRequestException.invalidWeekNum(bookingLimitRequest.getWeekNum());
    }
    BookingLimit bookingLimit = bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId,
        bookingLimitRequest.getWeekNum());
    if (bookingLimit != null) {
      bookingLimit.setBookingLimit(bookingLimitRequest.getBookingLimit());
    } else {
      bookingLimit = new BookingLimit(interviewerId,
          bookingLimitRequest.getWeekNum(),
          bookingLimitRequest.getBookingLimit());
    }

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
      throw NotFoundException.interviewerNotFound(interviewerId);
    }
    return bookingLimitRepository.findByInterviewerIdAndWeekNum(interviewerId, weekNum);
  }
}