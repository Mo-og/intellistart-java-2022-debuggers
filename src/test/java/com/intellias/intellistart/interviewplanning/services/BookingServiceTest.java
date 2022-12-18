package com.intellias.intellistart.interviewplanning.services;

import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.CANDIDATE_EMAIL;
import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.INTERVIEWER_ID;
import static com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils.interviewer;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.BookingImpl;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlotImpl;
import com.intellias.intellistart.interviewplanning.repositories.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.validators.BookingValidator;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  private static final InterviewerTimeSlotImpl INTERVIEWER_SLOT =
      new InterviewerTimeSlotImpl("08:00", "18:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlotImpl CANDIDATE_SLOT =
      new CandidateTimeSlotImpl(CANDIDATE_EMAIL, "2022-11-03", "08:00", "13:00");
  private static final BookingImpl BOOKING =
      new BookingImpl(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          CANDIDATE_SLOT,
          INTERVIEWER_SLOT,
          "some subject",
          "some desc"
      );

  private static final BookingDto BOOKING_DTO =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .build();

  private static final BookingDto BOOKING_DTO_WITH_WRONG_SLOT =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .build();

  static {
    INTERVIEWER_SLOT.setId(1L);
    INTERVIEWER_SLOT.setInterviewer(interviewer);
    CANDIDATE_SLOT.setId(1L);
    BOOKING.setId(1L);
    BOOKING_DTO.setId(1L);
    BOOKING_DTO.setInterviewerSlotId(1L);
    BOOKING_DTO.setCandidateSlotId(1L);
    BOOKING_DTO_WITH_WRONG_SLOT.setInterviewerSlotId(-1L);
    BOOKING_DTO_WITH_WRONG_SLOT.setCandidateSlotId(-1L);
  }

  @Mock
  BookingRepository bookingRepository;
  @Mock
  BookingLimitRepository bookingLimitRepository;
  @Mock
  InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  CandidateTimeSlotRepository candidateTimeSlotRepository;
  @Mock
  private BookingValidator bookingValidator;
  private BookingService service;

  @BeforeEach
  void setUp() {
    service = new BookingService(bookingRepository, bookingLimitRepository,
        interviewerTimeSlotRepository, candidateTimeSlotRepository, bookingValidator);
    interviewer.setId(INTERVIEWER_ID);
  }

  @Test
  void testCreateBooking() {
    when(interviewerTimeSlotRepository.findById(INTERVIEWER_SLOT.getId()))
        .thenReturn(Optional.of(INTERVIEWER_SLOT));
    when(candidateTimeSlotRepository.findById(CANDIDATE_SLOT.getId()))
        .thenReturn(Optional.of(CANDIDATE_SLOT));
    when(bookingLimitRepository
        .findByInterviewerIdAndWeekNum(INTERVIEWER_ID, INTERVIEWER_SLOT.getWeekNum()))
        .thenReturn(Optional.empty());
    when(bookingRepository
        .save(any()))
        .thenReturn(BOOKING);
    when(bookingLimitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    var createdBooking = service.createBooking(BOOKING_DTO);
    assertEquals(BOOKING_DTO, createdBooking);
  }

  @Test
  void testCreateBookingInterviewerSlotNotFound() {
    when(interviewerTimeSlotRepository.findById(-1L))
        .thenThrow(NotFoundException.timeSlot(-1L));
    assertThrows(NotFoundException.class, () -> service.createBooking(BOOKING_DTO_WITH_WRONG_SLOT));
  }

  @Test
  void testCreateBookingCandidateSlotNotFound() {
    when(interviewerTimeSlotRepository.findById(any()))
        .thenReturn(Optional.of(INTERVIEWER_SLOT));
    when(candidateTimeSlotRepository.findById(-1L))
        .thenThrow(NotFoundException.timeSlot(-1L));
    when(bookingLimitRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    assertThrows(NotFoundException.class, () -> service.createBooking(BOOKING_DTO_WITH_WRONG_SLOT));
  }

  @Test
  void testUpdateBooking() {
    when(bookingRepository
        .findById(1L))
        .thenReturn(Optional.of(BOOKING));
    when(bookingRepository
        .save(any()))
        .thenReturn(BOOKING);
    var retrievedBooking = service.updateBooking(1L, BOOKING_DTO);
    assertEquals(BOOKING_DTO.getFrom(), retrievedBooking.getFrom());
    assertEquals(BOOKING_DTO.getTo(), retrievedBooking.getTo());
    assertEquals(BOOKING_DTO.getSubject(), retrievedBooking.getSubject());
    assertEquals(BOOKING_DTO.getDescription(), retrievedBooking.getDescription());
  }

  @Test
  void testUpdateBookingNotFound() {
    when(bookingRepository
        .findById(2L))
        .thenThrow(NotFoundException.booking(2L));
    assertThrows(NotFoundException.class, () -> service.updateBooking(2L, BOOKING_DTO));
  }

  @Test
  void testDeleteBooking() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(BOOKING));
    doNothing().when(bookingRepository).delete(BOOKING);
    assertDoesNotThrow(() -> service.deleteBooking(1L));
  }

  @Test
  void testDeleteBookingNotFound() {
    when(bookingRepository.findById(2L)).thenThrow(NotFoundException.booking(2L));
    assertThrows(NotFoundException.class, () -> service.deleteBooking(2L));
  }

}
