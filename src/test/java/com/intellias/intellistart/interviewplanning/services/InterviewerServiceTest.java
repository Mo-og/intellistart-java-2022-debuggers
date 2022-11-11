package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  public static final String INTERVIEWER_EMAIL = "test.interviewer@test.com";
  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final User candidate = new User(CANDIDATE_EMAIL, UserRole.CANDIDATE);
  private static final InterviewerTimeSlot timeSlot = new InterviewerTimeSlot("09:00",
      "18:00", "Mon", WeekService.getNextWeekNum());
  private static final InterviewerTimeSlot timeSlotWithUser = new InterviewerTimeSlot(
      "09:00",
      "18:00", "Mon", WeekService.getNextWeekNum());
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(WeekService
          .getDateByWeekNumAndDayOfWeek(WeekService.getNextWeekNum(), DayOfWeek.MONDAY).toString(),
          "08:00", "13:00");
  private static final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(WeekService.getNextWeekNum())
          .dayOfWeek("Mon")
          .from(LocalTime.of(9, 0))
          .to(LocalTime.of(18, 0))
          .build();

  private static final Booking booking =
      new Booking(
          LocalTime.of(10, 0),
          LocalTime.of(11, 30),
          candidateSlot,
          timeSlot,
          "some subject",
          "some desc"
      );

  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(10, 0))
          .to(LocalTime.of(11, 30))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(timeSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();

  static {
    timeSlot.setId(1L);
    interviewerSlotDto.setId(1L);
    interviewerSlotDto.setBookings(Set.of(bookingDto));
    interviewer.setId(1L);
    candidate.setId(2L);
    timeSlotWithUser.setId(1L);
    timeSlotWithUser.setInterviewer(interviewer);
    candidateSlot.setId(2L);
    booking.setId(1L);
    bookingDto.setId(1L);
  }

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingRepository bookingRepository;

  private InterviewerService interviewerService;

  @BeforeEach
  void setService() {
    interviewerService = new InterviewerService(interviewerTimeSlotRepository, userRepository,
        bookingRepository);
  }

  @Test
  void testCreateSlot() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      when(userRepository.getReferenceById(1L)).thenReturn(interviewer);
      when(interviewerTimeSlotRepository.saveAndFlush(any(InterviewerTimeSlot.class)))
          .thenReturn(timeSlot);
      InterviewerTimeSlot createdSlot = interviewerService.createSlot(1L, timeSlot);
      assertEquals(timeSlot, createdSlot);
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> interviewerService.createSlot(1L, timeSlot));
    }
  }

  @Test
  void testGetSlot() {
    when(interviewerTimeSlotRepository
        .getReferenceById(1L))
        .thenReturn(timeSlotWithUser);
    var retrievedSlot = interviewerService.getSlotById(1L);
    assertEquals(1L, retrievedSlot.getId());
  }

  @Test
  void testGetRelevantInterviewerSlots() {
    Set<InterviewerTimeSlot> set = new HashSet<>();
    set.add(timeSlotWithUser);
    when(userRepository
        .existsById(1L))
        .thenReturn(true);
    when(interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNumGreaterThanEqual(1L,
            WeekService.getCurrentWeekNum()))
        .thenReturn(set);
    var retrievedSet = interviewerService
        .getRelevantInterviewerSlots(1L);
    assertEquals(set, retrievedSet);
  }

  @Test
  void testGetRelevantInterviewerSlotsForInvalidUser() {
    when(userRepository
        .existsById(-1L))
        .thenReturn(false);
    assertThrows(NotFoundException.class,
        () -> interviewerService.getRelevantInterviewerSlots(-1L));
  }

  @Test
  void testGetSlotsByWeekId() {
    when(userRepository
        .existsByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(true);
    when(interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(1L, WeekService.getNextWeekNum()))
        .thenReturn(Set.of(timeSlot));
    when(bookingRepository
        .findByInterviewerSlot(timeSlot))
        .thenReturn(Set.of(booking));
    var result = interviewerService
        .getSlotsByWeekId(1L, WeekService.getNextWeekNum());
    assertEquals(Set.of(interviewerSlotDto), result);
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerId() {
    when(userRepository
        .existsByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenThrow(NotFoundException.interviewer(-1L));
    int weekNum = WeekService.getNextWeekNum();
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(-1L, weekNum));
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerRole() {
    when(userRepository
        .existsByIdAndRole(2L, UserRole.INTERVIEWER))
        .thenThrow(NotFoundException.interviewer(2L));
    int weekNum = WeekService.getNextWeekNum();
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(2L, weekNum));
  }

  @Test
  void testGetInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(timeSlot)).thenReturn(Set.of(booking));
    var result = interviewerService
        .getInterviewerSlotsWithBookings(Set.of(timeSlot));
    assertEquals(Set.of(interviewerSlotDto), result);
  }

  @Test
  void testUpdateSlot() {
    if (LocalDate.now().getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()) {
      when(interviewerTimeSlotRepository
          .getReferenceById(1L))
          .thenReturn(new InterviewerTimeSlot());
      when(interviewerTimeSlotRepository
          .save(any()))
          .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
      when(interviewerService.getSlotById(1L)).thenReturn(timeSlotWithUser);

      var slot = interviewerService
          .updateSlot(1L, 1L, timeSlotWithUser);


      assertEquals(timeSlotWithUser.getFrom(), slot.getFrom());
      assertEquals(timeSlotWithUser.getTo(), slot.getTo());
      assertEquals(timeSlotWithUser.getDayOfWeek(), slot.getDayOfWeek());
      assertEquals(timeSlotWithUser.getWeekNum(), slot.getWeekNum());
    } else {
      assertThrows(ApplicationErrorException.class,
          () -> interviewerService.updateSlot(1L, 1L, timeSlotWithUser));
    }
  }

  @Test
  void testGetById() {
    when(userRepository
        .getReferenceById(1L))
        .thenReturn(interviewer);
    assertEquals(interviewer, interviewerService.getById(1L));
  }

  @Test
  void testGetByWrongId() {
    when(userRepository
        .getReferenceById(-1L))
        .thenThrow(new EntityNotFoundException());
    assertThrows(NotFoundException.class, () -> interviewerService.getById(-1L));
  }
}