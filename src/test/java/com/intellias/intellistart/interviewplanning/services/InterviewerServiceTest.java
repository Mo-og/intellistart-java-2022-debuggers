package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import com.intellias.intellistart.interviewplanning.validators.InterviewerSlotValidator;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  @Spy
  private WeekServiceImp weekService;
  private final WeekService actualWeekService = new WeekServiceImp();
  public static final String INTERVIEWER_EMAIL = "test.interviewer@test.com";
  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private final InterviewerTimeSlot timeSlot = new InterviewerTimeSlot("09:00",
      "18:00", "Mon", actualWeekService.getNextWeekNum());
  private final InterviewerTimeSlot timeSlotWithUser = new InterviewerTimeSlot(
      "09:00",
      "18:00", "Mon", actualWeekService.getNextWeekNum());
  private final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(CANDIDATE_EMAIL, actualWeekService
          .getDateByWeekNumAndDayOfWeek(actualWeekService.getNextWeekNum(), DayOfWeek.MONDAY)
          .toString(),
          "08:00", "13:00");
  private final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(actualWeekService.getNextWeekNum())
          .dayOfWeek("Mon")
          .from(LocalTime.of(9, 0))
          .to(LocalTime.of(18, 0))
          .build();

  private final Booking booking =
      new Booking(
          LocalTime.of(10, 0),
          LocalTime.of(11, 30),
          candidateSlot,
          timeSlot,
          "some subject",
          "some desc"
      );

  private final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(10, 0))
          .to(LocalTime.of(11, 30))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(timeSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();

  {
    timeSlot.setId(1L);
    interviewerSlotDto.setId(1L);
    interviewerSlotDto.setBookings(Set.of(bookingDto));
    interviewer.setId(1L);
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
        bookingRepository, weekService, new InterviewerSlotValidator(weekService));
  }

  @Test
  void testCreateSlot() {
    when(weekService.getNowDay()).thenReturn(DayOfWeek.MONDAY);
    when(userRepository.getReferenceById(1L)).thenReturn(interviewer);
    when(interviewerTimeSlotRepository.saveAndFlush(any(InterviewerTimeSlot.class)))
        .thenReturn(timeSlot);
    InterviewerTimeSlot createdSlot = interviewerService.createSlot(1L, timeSlot);
    assertEquals(timeSlot, createdSlot);
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
            actualWeekService.getCurrentWeekNum()))
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
        .findByInterviewerIdAndWeekNum(1L, actualWeekService.getNextWeekNum()))
        .thenReturn(Set.of(timeSlot));
    when(bookingRepository
        .findByInterviewerSlot(timeSlot))
        .thenReturn(Set.of(booking));
    var result = interviewerService
        .getSlotsByWeekId(1L, actualWeekService.getNextWeekNum());
    assertEquals(Set.of(interviewerSlotDto), result);
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerId() {
    when(userRepository
        .existsByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenThrow(NotFoundException.interviewer(-1L));
    int weekNum = actualWeekService.getNextWeekNum();
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(-1L, weekNum));
  }

  @Test
  void testGetSlotsByWeekIdWithWrongInterviewerRole() {
    when(userRepository
        .existsByIdAndRole(2L, UserRole.INTERVIEWER))
        .thenThrow(NotFoundException.interviewer(2L));
    int weekNum = actualWeekService.getNextWeekNum();
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
    when(weekService.getNowDay()).thenReturn(DayOfWeek.MONDAY);
    System.out.println(weekService.getNowDay());
    InterviewerTimeSlot interviewerTimeSlot = new InterviewerTimeSlot();
    interviewerTimeSlot.setInterviewer(interviewer);
    when(userRepository.getReferenceById(1L)).thenReturn(interviewer);
    when(interviewerTimeSlotRepository
        .getReferenceById(1L))
        .thenReturn(interviewerTimeSlot);
    when(interviewerTimeSlotRepository
        .save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    when(weekService.getNextWeekNum()).thenCallRealMethod();
    System.out.println(weekService.getNextWeekNum());
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
    assertThrows(NotFoundException.class, () -> interviewerService.getById(-1L));
  }

  @Test
  void testThrowExceptionGetSlotsByWeekId() {
    when(userRepository
        .existsByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(false);
    assertThrows(NotFoundException.class,
        () -> interviewerService.getSlotsByWeekId(1L, actualWeekService.getCurrentWeekNum()));
  }

  @Test
  void test() {
    when(weekService.getNowDay()).thenReturn(DayOfWeek.MONDAY);
    System.out.println(weekService.getNowDay());
  }
}