package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.mapper.InterviewerSlotMapper;
import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
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
      "18:00", "Mon", 202210);
  private static final InterviewerTimeSlot timeSlotWithUser = new InterviewerTimeSlot(
      "09:00",
      "18:00", "Mon", 202210);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot("2022-03-07", "08:00", "13:00");
  private static final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(202210)
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

  static {
    interviewer.setId(1L);
    candidate.setId(2L);
    timeSlotWithUser.setId(1L);
    timeSlotWithUser.setInterviewer(interviewer);
    candidateSlot.setId(2L);
  }

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private InterviewerSlotMapper interviewerSlotMapper;

  private InterviewerService interviewerService;

  @BeforeEach
  void setService() {
    interviewerService = new InterviewerService(interviewerTimeSlotRepository, userRepository,
        bookingRepository, interviewerSlotMapper);
  }

  @Test
  void testCreateSlot() {
    when(userRepository
        .getReferenceById(1L))
        .thenReturn(interviewer);
    when(interviewerTimeSlotRepository
        .saveAndFlush(isA(InterviewerTimeSlot.class)))
        .thenReturn(timeSlotWithUser);
    var retrievedSlot = interviewerService.createSlot(1L, timeSlot);
    assertEquals(interviewer, retrievedSlot.getInterviewer());
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
    assertThrows(InterviewerNotFoundException.class,
        () -> interviewerService.getRelevantInterviewerSlots(-1L));
  }

  @Test
  void testGetSlotsByWeekId() {
    when(userRepository
        .existsByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(true);
    when(interviewerTimeSlotRepository
        .findByInterviewerIdAndWeekNum(1L, 202210))
        .thenReturn(Set.of(timeSlot));
    when(bookingRepository
        .findByInterviewerSlot(timeSlot))
        .thenReturn(Set.of(booking));
    when(interviewerSlotMapper
        .mapToInterviewerSlotWithBookingsDto(timeSlot, Set.of(booking)))
        .thenReturn(interviewerSlotDto);

    var result = interviewerService
        .getSlotsByWeekId(1L, 202210);
    assertEquals(Set.of(interviewerSlotDto), result);
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerId() {
    when(userRepository
        .existsByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenThrow(new InterviewerNotFoundException(-1L));
    assertThrows(InterviewerNotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(-1L, 202210));
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerRole() {
    when(userRepository
        .existsByIdAndRole(2L, UserRole.INTERVIEWER))
        .thenThrow(new InterviewerNotFoundException(2L));
    assertThrows(InterviewerNotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(2L, 202210));
  }

  @Test
  void testGetInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(timeSlot)).thenReturn(Set.of(booking));
    when(interviewerSlotMapper.mapToInterviewerSlotWithBookingsDto(timeSlot, Set.of(booking)))
        .thenReturn(interviewerSlotDto);
    var result = interviewerService
        .getInterviewerSlotsWithBookings(Set.of(timeSlot));
    assertEquals(Set.of(interviewerSlotDto), result);
  }

  @Test
  void testUpdateSlot() {
    when(interviewerTimeSlotRepository
        .getReferenceById(1L))
        .thenReturn(new InterviewerTimeSlot());
    when(interviewerTimeSlotRepository
        .save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    var slot = interviewerService
        .updateSlot(1L, 1L, timeSlot);
    assertEquals(timeSlot.getFrom(), slot.getFrom());
    assertEquals(timeSlot.getTo(), slot.getTo());
    assertEquals(timeSlot.getDayOfWeek(), slot.getDayOfWeek());
    assertEquals(timeSlot.getWeekNum(), slot.getWeekNum());
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
    assertThrows(InterviewerNotFoundException.class, () -> interviewerService.getById(-1L));
  }
}
