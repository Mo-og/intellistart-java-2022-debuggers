package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.DayDashboardDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
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
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
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

  private final WeekService weekService = new WeekServiceImp();

  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  private static final User coordinator = new User("coordinator@gmail.com",
      UserRole.COORDINATOR);
  private static final User interviewer = new User("interviewer@gmail.com",
      UserRole.INTERVIEWER);
  private final CandidateTimeSlot candidateSlot = new CandidateTimeSlot(CANDIDATE_EMAIL,
      weekService.getCurrentDate().toString(), "08:00", "13:00");
  private final CandidateSlotDto candidateSlotDto =
      CandidateSlotDto.builder()
          .date(weekService.getCurrentDate())
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(13, 0))
          .build();
  private final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("09:00", "18:00", "Mon",
          weekService.getCurrentWeekNum());

  private final InterviewerSlotDto interviewerSlotDto =
      InterviewerSlotDto.builder()
          .weekNum(weekService.getCurrentWeekNum())
          .dayOfWeek("Mon")
          .from(LocalTime.of(9, 0))
          .to(LocalTime.of(18, 0))
          .build();
  private final Booking booking =
      new Booking(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          candidateSlot,
          interviewerSlot,
          "some subject",
          "some desc"
      );
  private final BookingDto bookingDto =
      BookingDto.builder()
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(10, 0))
          .subject("some subject")
          .description("some desc")
          .interviewerSlotId(interviewerSlot.getId())
          .candidateSlotId(candidateSlot.getId())
          .build();
  private final DayDashboardDto mon = DayDashboardDto.builder()
      .date(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
          DayOfWeek.MONDAY))
      .dayOfWeek("Mon")
      .build();
  private final DayDashboardDto tue = DayDashboardDto.builder()
      .date(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
          DayOfWeek.TUESDAY))
      .dayOfWeek("Tue")
      .build();
  private final DayDashboardDto wed = DayDashboardDto.builder()
      .date(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
          DayOfWeek.WEDNESDAY))
      .dayOfWeek("Wed")
      .build();
  private final DayDashboardDto thu = DayDashboardDto.builder()
      .date(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
          DayOfWeek.THURSDAY))
      .dayOfWeek("Thu")
      .build();
  private final DayDashboardDto fri = DayDashboardDto.builder()
      .date(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
          DayOfWeek.FRIDAY))
      .dayOfWeek("Fri")
      .build();
  private static final Set<DayDashboardDto> days = new TreeSet<>(
      Comparator.comparing(DayDashboardDto::getDate));
  private static final DashboardDto weekDashboard = new DashboardDto();
  private static final Set<InterviewerSlotDto> interviewerSlotDtoSet = new TreeSet<>(
      Comparator.comparing(InterviewerSlotDto::getFrom));
  private static final Set<CandidateSlotDto> candidateSlotDtoSet = new TreeSet<>(
      Comparator.comparing(CandidateSlotDto::getFrom));

  {
    interviewer.setId(1L);
    interviewerSlot.setId(1L);
    interviewerSlot.setInterviewer(interviewer);
    candidateSlot.setId(1L);
    coordinator.setId(1L);

    booking.setCandidateSlot(candidateSlot);
    booking.setInterviewerSlot(interviewerSlot);
    booking.setId(1L);
    bookingDto.setId(1L);
    bookingDto.setCandidateSlotId(candidateSlot.getId());
    bookingDto.setInterviewerSlotId(interviewerSlot.getId());

    interviewerSlotDtoSet.add(interviewerSlotDto);
    candidateSlotDtoSet.add(candidateSlotDto);

    mon.setInterviewerSlots(interviewerSlotDtoSet);
    mon.setCandidateSlots(candidateSlotDtoSet);
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
    service = new CoordinatorService(weekService, interviewerTimeSlotRepository,
        candidateTimeSlotRepository, bookingRepository, userRepository);
  }

  @Test
  void testGetWeekDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(Set.of(interviewerSlot));
    when(candidateTimeSlotRepository
        .findByDate(weekService
            .getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(Set.of(candidateSlot));
    when(bookingRepository
        .findByCandidateSlotDate(
            weekService
                .getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(Set.of(booking));
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(Set.of(booking));
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(Set.of(booking));
    var dashboard = service.getWeekDashboard(weekService.getCurrentWeekNum());
    assertEquals(weekDashboard, dashboard);
  }

  @Test
  void testGetDayDashboard() {
    when(interviewerTimeSlotRepository
        .findByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY))
        .thenReturn(Set.of(interviewerSlot));
    when(candidateTimeSlotRepository
        .findByDate(weekService.getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(),
            DayOfWeek.MONDAY)))
        .thenReturn(Set.of(candidateSlot));
    when(bookingRepository
        .findByCandidateSlotDate(weekService
            .getDateByWeekNumAndDayOfWeek(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY)))
        .thenReturn(Set.of(booking));
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(Set.of(booking));
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(Set.of(booking));
    var dashboard =
        service.getDayDashboard(weekService.getCurrentWeekNum(), DayOfWeek.MONDAY);
    assertEquals(mon, dashboard);
  }

  @Test
  void testInterviewerSlotsWithBookings() {
    when(bookingRepository.findByInterviewerSlot(interviewerSlot)).thenReturn(Set.of(booking));
    var result = service
        .getInterviewerSlotsWithBookings(Set.of(interviewerSlot));
    assertEquals(interviewerSlotDtoSet, result);
  }

  @Test
  void testGetCandidateSlotsWithBookings() {
    when(bookingRepository.findByCandidateSlot(candidateSlot)).thenReturn(Set.of(booking));
    var result = service.getCandidateSlotsWithBookings(Set.of(candidateSlot));
    assertEquals(candidateSlotDtoSet, result);
  }

  @Test
  void testGetBookingMap() {
    var result = service.getBookingMap(Set.of(booking));
    assertEquals(Map.of(bookingDto.getId(), bookingDto), result);
  }

  @Test
  void testGrantInterviewerRole() {
    when(userRepository.findByEmail("interviewer@gmail.com"))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(interviewer);
    var result = service.grantInterviewerRole("interviewer@gmail.com");
    assertEquals(interviewer, result);
  }

  @Test
  void testGrantCoordinatorRole() {
    when(userRepository.findByEmail("coordinator@gmail.com"))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(coordinator);
    when(interviewerTimeSlotRepository.findByInterviewer(interviewer)).thenReturn(List.of(interviewerSlot));
    var result = service.grantCoordinatorRole("coordinator@gmail.com");
    assertEquals(coordinator, result);
  }

  @Test
  void testRevokeInterviewerRole() {
    when(userRepository.findByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.of(interviewer));
    assertEquals(UserRole.INTERVIEWER, service.revokeInterviewerRole(1L).getRole());
  }

  @Test
  void testRevokeInterviewerRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> service.revokeInterviewerRole(-1L));
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(userRepository.findByIdAndRole(1L, UserRole.COORDINATOR))
        .thenReturn(Optional.of(coordinator));
    assertEquals(UserRole.COORDINATOR, service.revokeCoordinatorRole(1L).getRole());
  }

  @Test
  void testRevokeCoordinatorRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.COORDINATOR))
        .thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> service.revokeCoordinatorRole(-1L));
  }

  @Test
  void testGetUsersWithRole() {
    Set<User> set = Set.of(interviewer);
    when(userRepository.findByRole(UserRole.INTERVIEWER)).thenReturn(set);
    assertEquals(set, service.getUsersWithRole(UserRole.INTERVIEWER));
  }
}