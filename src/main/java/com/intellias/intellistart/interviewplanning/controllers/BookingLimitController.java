package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.validators.PermissionValidator.checkAuthorized;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.services.BookingLimitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for managing booking limits.
 */
@RestController
@RequiredArgsConstructor
public class BookingLimitController {

  private final BookingLimitService bookingLimitService;

  /**
   * Endpoint to set booking limit for interviewer.
   *
   * @param interviewerId   id of the interviewer
   * @param bookingLimitDto booking limit information
   * @param auth            object from spring security containing the principle presented by user
   * @return booking limit
   */
  @PostMapping("/interviewers/{interviewerId}/bookingLimits")
  public BookingLimit setBookingLimit(@PathVariable Long interviewerId,
      @RequestBody BookingLimitDto bookingLimitDto, Authentication auth) {
    checkAuthorized(auth, interviewerId);
    return bookingLimitService.saveBookingLimit(interviewerId, bookingLimitDto);
  }

  /**
   * Endpoint to get week booking limits.
   *
   * @param weekNum number of the week
   * @return booking limits for the week
   */
  @GetMapping("/interviewers/bookingLimits/{weekNum}")
  public List<BookingLimit> getWeekBookingLimits(@PathVariable Integer weekNum) {
    return bookingLimitService.getBookingLimitsByWeekNum(weekNum);
  }

  /**
   * Endpoint to get interviewer booking limit.
   *
   * @param interviewerId id of the interviewer
   * @param weekNum       number of the week
   * @return booking limit for the interviewer
   */
  @GetMapping("/interviewers/{interviewerId}/bookingLimits/{weekNum}")
  public BookingLimit findBookingLimit(@PathVariable Long interviewerId,
      @PathVariable Integer weekNum) {
    return bookingLimitService.findBookingLimit(interviewerId, weekNum);
  }
}
