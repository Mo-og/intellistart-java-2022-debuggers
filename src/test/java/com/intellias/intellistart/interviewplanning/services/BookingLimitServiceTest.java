package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekEditException;
import com.intellias.intellistart.interviewplanning.models.BookingLimit;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingLimitServiceTest {

  private BookingLimitService bookingLimitService;
  @Mock
  private BookingLimitRepository bookingLimitRepository;
  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setService() {
    bookingLimitService = new BookingLimitService(bookingLimitRepository, userRepository);
  }

  private static final int limit = 5;
  private static final int nextWeekNum = WeekService.getNextWeekNum();
  private static final Long existingUserId = 1L;
  private static final Long notExistingUserId = 2L;

  private static final BookingLimit bookingLimit = new BookingLimit(
      existingUserId,
      nextWeekNum,
      limit);

  @Test
  void testSetBooking() {
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenReturn(bookingLimit);
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    when(bookingLimitRepository.save(bookingLimit))
        .thenReturn(bookingLimit);

    BookingLimit newBookingLimit = bookingLimitService.setBookingLimit(limit, existingUserId,
        nextWeekNum);
    assertEquals(bookingLimit.getId(), newBookingLimit.getId());
    assertEquals(bookingLimit.getInterviewerId(), newBookingLimit.getInterviewerId());
    assertEquals(bookingLimit.getBookingLimit(), newBookingLimit.getBookingLimit());
  }

  @Test
  void testSetBookingThrowUserException() {
    when(userRepository.existsById(notExistingUserId))
        .thenReturn(false);
    assertThrows(UserNotFoundException.class,
        () -> bookingLimitService.setBookingLimit(limit, notExistingUserId,
            nextWeekNum));
  }

  @Test
  void testSetBookingThrowWeekNumException() {
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    assertThrows(WeekEditException.class,
        () -> bookingLimitService.setBookingLimit(limit, existingUserId, 0));
  }

  @Test
  void testSetBookingNewBooking() {
    when(bookingLimitRepository.findByInterviewerIdAndWeekNum(existingUserId, nextWeekNum))
        .thenReturn(null);
    when(userRepository.existsById(existingUserId))
        .thenReturn(true);
    when(bookingLimitRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    BookingLimit newBookingLimit = bookingLimitService.setBookingLimit(limit, existingUserId,
        nextWeekNum);
    assertEquals(bookingLimit.getId(), newBookingLimit.getId());
    assertEquals(bookingLimit.getInterviewerId(), newBookingLimit.getInterviewerId());
    assertEquals(bookingLimit.getBookingLimit(), newBookingLimit.getBookingLimit());
  }
}