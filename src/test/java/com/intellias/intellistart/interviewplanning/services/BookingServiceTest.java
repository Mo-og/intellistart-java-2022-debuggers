package com.intellias.intellistart.interviewplanning.services;

import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.CANDIDATE_EMAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  private static final InterviewerTimeSlot INTERVIEWER_SLOT =
      new InterviewerTimeSlot("08:00", "18:00", "WEDNESDAY", 202240);
  private static final CandidateTimeSlot CANDIDATE_SLOT =
      new CandidateTimeSlot(CANDIDATE_EMAIL, "2022-11-03", "08:00", "13:00");
  private static final Booking BOOKING =
      new Booking(
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
    CANDIDATE_SLOT.setId(1L);
    BOOKING.setId(1L);
    BOOKING_DTO.setId(1L);
    BOOKING_DTO.setInterviewerSlotId(1L);
    BOOKING_DTO.setCandidateSlotId(1L);
    BOOKING_DTO_WITH_WRONG_SLOT.setInterviewerSlotId(-1L);
  }

  @Mock
  BookingRepository bookingRepository;
  @Mock
  InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  CandidateTimeSlotRepository candidateTimeSlotRepository;
  private BookingService service;

  @BeforeEach
  void setService() {
    service = new BookingService(bookingRepository, interviewerTimeSlotRepository,
        candidateTimeSlotRepository);
  }

  @Test
  void testCreateBooking() {
    when(interviewerTimeSlotRepository.findById(INTERVIEWER_SLOT.getId()))
        .thenReturn(Optional.of(INTERVIEWER_SLOT));
    when(candidateTimeSlotRepository.findById(CANDIDATE_SLOT.getId()))
        .thenReturn(Optional.of(CANDIDATE_SLOT));
    when(bookingRepository
        .save(any()))
        .thenReturn(BOOKING);
    var createdBooking = service.createBooking(BOOKING_DTO);
    assertEquals(BOOKING_DTO, createdBooking);
  }

  @Test
  void testCreateBookingSlotNotFound() {
    when(interviewerTimeSlotRepository.findById(-1L))
        .thenThrow(NotFoundException.timeSlot(-1L));
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
