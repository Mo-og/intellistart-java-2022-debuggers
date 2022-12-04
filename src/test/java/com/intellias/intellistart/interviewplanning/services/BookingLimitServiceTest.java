package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingLimitServiceTest {

  private BookingLimitService bookingLimitService;
  @Mock
  private BookingLimitRepository bookingLimitRepository;
  @Mock
  private UserRepository userRepository;
  private final WeekService weekService = new WeekServiceImp();

  @BeforeEach
  void setService() {
    bookingLimitService = new BookingLimitService(bookingLimitRepository, userRepository,
        weekService);
  }

  private static final int limit = 5;
  private final int nextWeekNum = weekService.getNextWeekNum();
  private static final Long existingUserId = 1L;
  private static final Long notExistingUserId = 2L;

  private final BookingLimit bookingLimit = new BookingLimit(
      existingUserId,
      nextWeekNum,
      limit);
  private final BookingLimit bookingLimit2 = new BookingLimit(
      notExistingUserId,
      nextWeekNum,
      limit + 2);
  private final BookingLimitDto bookingLimitRequest = new BookingLimitDto(limit,
      nextWeekNum);
  private static final BookingLimitDto badBookingLimitRequest = new BookingLimitDto(limit,
      0);

  @Test
  void testSetBooking() {
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenReturn(Optional.of(bookingLimit));
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    when(bookingLimitRepository.save(bookingLimit))
        .thenReturn(bookingLimit);

    BookingLimit newBookingLimit = bookingLimitService.saveBookingLimit(existingUserId,
        bookingLimitRequest);
    assertEquals(bookingLimit.getId(), newBookingLimit.getId());
    assertEquals(bookingLimit.getInterviewerId(), newBookingLimit.getInterviewerId());
    assertEquals(bookingLimit.getBookingLimit(), newBookingLimit.getBookingLimit());
  }

  @Test
  void testSetBookingThrowUserException() {
    lenient().when(userRepository.existsById(notExistingUserId))
        .thenReturn(false);
    assertThrows(NotFoundException.class,
        () -> bookingLimitService.saveBookingLimit(existingUserId, bookingLimitRequest));
  }

  @Test
  void testSetBookingThrowWeekNumException() {
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    assertThrows(ApplicationErrorException.class,
        () -> bookingLimitService.saveBookingLimit(existingUserId, badBookingLimitRequest));
  }

  @Test
  void testSetBookingNewBooking() {
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenReturn(Optional.of(bookingLimit));
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    when(bookingLimitRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    BookingLimit newBookingLimit = bookingLimitService.saveBookingLimit(existingUserId,
        bookingLimitRequest);
    assertEquals(bookingLimit.getId(), newBookingLimit.getId());
    assertEquals(bookingLimit.getInterviewerId(), newBookingLimit.getInterviewerId());
    assertEquals(bookingLimit.getBookingLimit(), newBookingLimit.getBookingLimit());
  }

  @Test
  void testGetWeekBookingLimits() {
    when(bookingLimitService.getBookingLimitsByWeekNum(nextWeekNum))
        .thenReturn(List.of(bookingLimit, bookingLimit2));
    assertEquals(bookingLimitService.getBookingLimitsByWeekNum(nextWeekNum),
        List.of(bookingLimit, bookingLimit2));
  }

  @Test
  void testGetBookingLimitOk() {
    lenient().when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenReturn(Optional.of(bookingLimit));
    assertEquals(bookingLimitService.findBookingLimit(existingUserId, nextWeekNum),
        bookingLimit);
  }

  @Test
  void testGetBookingLimitThrowUserException() {
    when(userRepository.existsById(existingUserId))
        .thenReturn(false);
    assertThrows(NotFoundException.class,
        () -> bookingLimitService.findBookingLimit(existingUserId, nextWeekNum));
  }

  @Test
  void testThrowBookingLimitExceptionFindBookingLimit() {
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenThrow(NotFoundException.bookingLimit(existingUserId, nextWeekNum));
    assertThrows(NotFoundException.class,
        () -> bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum));
  }
}
