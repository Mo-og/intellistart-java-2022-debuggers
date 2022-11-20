package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DayDashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorServiceTest {

  public static final String COORDINATOR_EMAIL = "coordinator@gmail.com";
  public static final String INTERVIEWER_EMAIL = "interviewer@gmail.com";
  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final User coordinator = new User(COORDINATOR_EMAIL, UserRole.COORDINATOR);
  private static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final CandidateTimeSlot candidateSlot = new CandidateTimeSlot(CANDIDATE_EMAIL,
      WeekService.getCurrentDate().toString(), "08:00", "13:00");
  private static final CandidateSlotDto candidateSlotDto =
      CandidateSlotDto.builder()
          .date(WeekService.getCurrentDate())
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(13, 0))
          .build();
  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("09:00", "18:00", "Mon",
          WeekService.getCurrentWeekNum());

  private static final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(WeekService.getCurrentWeekNum())
          .dayOfWeek("Mon")
          .from(LocalTime.of(9, 0))
          .to(LocalTime.of(18, 0))
          .build();
  private static final Booking booking =
      new Booking(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          candidateSlot,
          interviewerSlot,
          "some subject",
          "some desc"
      );
  private static final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(interviewerSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();
  private static final DayDashboardDto mon = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.MONDAY))
      .dayOfWeek("Mon")
      .build();
  private static final DayDashboardDto tue = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.TUESDAY))
      .dayOfWeek("Tue")
      .build();
  private static final DayDashboardDto wed = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.WEDNESDAY))
      .dayOfWeek("Wed")
      .build();
  private static final DayDashboardDto thu = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.THURSDAY))
      .dayOfWeek("Thu")
      .build();
  private static final DayDashboardDto fri = DayDashboardDto.builder()
      .date(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
          DayOfWeek.FRIDAY))
      .dayOfWeek("Fri")
      .build();
  private static final Set<DayDashboardDto> days = new TreeSet<>(
      Comparator.comparing(DayDashboardDto::getDate));
  private static final DashboardDto weekDashboard = new DashboardDto();

  static {
    interviewer.setId(1L);
    interviewerSlot.setId(1L);
    interviewerSlotDto.setId(1L);
    interviewerSlot.setInterviewer(interviewer);
    interviewerSlotDto.setBookings(List.of(bookingDto));
    candidateSlot.setId(1L);
    candidateSlotDto.setId(1L);
    candidateSlotDto.setBookings(List.of(bookingDto));
    coordinator.setId(1L);

    booking.setCandidateSlot(candidateSlot);
    booking.setInterviewerSlot(interviewerSlot);
    booking.setId(1L);
    bookingDto.setId(1L);
    bookingDto.setCandidateSlotId(candidateSlot.getId());
    bookingDto.setInterviewerSlotId(interviewerSlot.getId());

    mon.setInterviewerSlots(List.of(interviewerSlotDto));
    mon.setCandidateSlots(List.of(candidateSlotDto));
    mon.setBookings(Map.of(bookingDto.getId(), bookingDto));
    days.add(mon);
    days.add(tue);
    days.add(wed);
    days.add(thu);
    days.add(fri);
    weekDashboard.setDays(days);
  }

  @Mock
  BookingRepository bookingRepository;
  @Mock
  CandidateTimeSlotRepository candidateTimeSlotRepository;
  @Mock
  InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  UserRepository userRepository;

  private CoordinatorService service;

  @BeforeEach
  void setService() {
    service = new CoordinatorService(interviewerTimeSlotRepository, candidateTimeSlotRepository,
        bookingRepository, userRepository);
  }

  @Test
  void testGetWeekDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(List.of(interviewerSlot));
    when(candidateTimeSlotRepository
        .findByDate(WeekService
            .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(List.of(candidateSlot));
    when(bookingRepository
        .findByCandidateSlotDate(
            WeekService
                .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(List.of(booking));
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(List.of(booking));
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(List.of(booking));
    var dashboard = service.getWeekDashboard(WeekService.getCurrentWeekNum());
    assertEquals(weekDashboard, dashboard);
  }

  @Test
  void testGetDayDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(List.of(interviewerSlot));
    when(candidateTimeSlotRepository
        .findByDate(WeekService.getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(),
            DayOfWeek.MONDAY)))
        .thenReturn(List.of(candidateSlot));
    when(bookingRepository
        .findByCandidateSlotDate(WeekService
            .getDateByWeekNumAndDayOfWeek(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(List.of(booking));
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(List.of(booking));
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(List.of(booking));
    var dashboard =
        service.getDayDashboard(WeekService.getCurrentWeekNum(), DayOfWeek.MONDAY);
    assertEquals(mon, dashboard);
  }

  @Test
  void testInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(List.of(booking));
    var result = service
        .getInterviewerSlotsWithBookings(List.of(interviewerSlot));
    assertEquals(List.of(interviewerSlotDto), result);
  }

  @Test
  void testGetCandidateSlotsWithBookings() {
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(List.of(booking));
    var result = service.getCandidateSlotsWithBookings(List.of(candidateSlot));
    assertEquals(List.of(candidateSlotDto), result);
  }

  @Test
  void testGetBookingMap() {
    var result = service.getBookingMap(List.of(booking));
    assertEquals(Map.of(bookingDto.getId(), bookingDto), result);
  }

  @Test
  void testGrantInterviewerRole() {
    when(userRepository.findByEmail(INTERVIEWER_EMAIL))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(interviewer);
    var result = service.grantInterviewerRole(INTERVIEWER_EMAIL, COORDINATOR_EMAIL);
    assertEquals(interviewer, result);
  }

  @Test
  void testSelfGrantInterviewerRole() {
    assertThrows(ApplicationErrorException.class,
        () -> service.grantInterviewerRole(COORDINATOR_EMAIL, COORDINATOR_EMAIL));
  }

  @Test
  void testGrantCoordinatorRole() {
    when(userRepository.findByEmail(COORDINATOR_EMAIL))
        .thenReturn(Optional.of(coordinator));
    when(userRepository.save(coordinator))
        .thenReturn(coordinator);
    var result = service.grantCoordinatorRole(COORDINATOR_EMAIL);
    assertEquals(coordinator, result);
  }

  @Test
  void testRevokeInterviewerRole() {
    when(userRepository.findById(1L))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(interviewer);
    assertEquals(UserRole.CANDIDATE, service.revokeInterviewerRole(1L).getRole());
  }

  @Test
  void testRevokeInterviewerRoleWrongId() {
    when(userRepository.findById(-1L))
        .thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> service.revokeInterviewerRole(-1L));
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(userRepository.findById(1L))
        .thenReturn(Optional.of(coordinator));
    when(userRepository.save(coordinator))
        .thenReturn(coordinator);
    assertEquals(UserRole.CANDIDATE, service.revokeCoordinatorRole(1L, 2L).getRole());
  }

  @Test
  void testSelfRevokeCoordinatorRole() {
    assertThrows(ApplicationErrorException.class,
        () -> service.revokeCoordinatorRole(1L, 1L));
  }

  @Test
  void testRevokeCoordinatorRoleWrongId() {
    when(userRepository.findById(-1L))
        .thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> service.revokeCoordinatorRole(-1L, 1L));
  }

  @Test
  void testGetUsersWithRole() {
    when(userRepository.findByRole(UserRole.INTERVIEWER)).thenReturn(List.of(interviewer));
    assertEquals(List.of(interviewer), service.getUsersWithRole(UserRole.INTERVIEWER));
  }
}