package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.CoordinatorNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Booking;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService.Dashboard;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinatorServiceTest {

  private static final User coordinator = new User("coordinator@gmail.com",
      UserRole.COORDINATOR);
  private static final User candidate = new User("cand@gmail.com",
      UserRole.CANDIDATE);
  private static final User interviewer = new User("interviewer@gmail.com",
      UserRole.INTERVIEWER);
  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot(WeekService.getCurrentDate().toString(), "08:00", "13:00");
  private static final InterviewerTimeSlot interviewerSlot =
      new InterviewerTimeSlot("09:00", "18:00", "Mon",
          WeekService.getCurrentWeekNum());
  private static final Booking booking =
      new Booking(
          LocalTime.of(8, 0),
          LocalTime.of(10, 0),
          candidateSlot,
          interviewerSlot,
          "some subject",
          "some desc"
      );
  private static final Map<DayOfWeek, TreeSet<InterviewerTimeSlot>> interviewerTimeSlotsMap
      = new HashMap<>();
  private static final Map<DayOfWeek, TreeSet<CandidateTimeSlot>> candidateTimeSlotsMap
      = new HashMap<>();
  private static final Map<DayOfWeek, TreeSet<Booking>> bookingsMap
      = new HashMap<>();
  private static final Dashboard dashboard =
      new Dashboard(interviewerTimeSlotsMap, candidateTimeSlotsMap, bookingsMap);

  static {
    interviewer.setId(1L);
    interviewerSlot.setId(1L);
    interviewerSlot.setInterviewer(interviewer);
    candidate.setId(1L);
    candidateSlot.setCandidate(candidate);
    coordinator.setId(1L);
    booking.setCandidateSlot(candidateSlot);
    booking.setInterviewerSlot(interviewerSlot);

    TreeSet<InterviewerTimeSlot> interviewerSlotTreeSet = new TreeSet<>(
        Comparator.comparing(InterviewerTimeSlot::getFrom));
    interviewerSlotTreeSet.add(interviewerSlot);
    interviewerTimeSlotsMap.put(interviewerSlot.getDayOfWeek(), interviewerSlotTreeSet);

    TreeSet<CandidateTimeSlot> candidateSlotTreeSet = new TreeSet<>(
        Comparator.comparing(CandidateTimeSlot::getFrom));
    candidateSlotTreeSet.add(candidateSlot);
    candidateTimeSlotsMap.put(candidateSlot.getDate().getDayOfWeek(), candidateSlotTreeSet);

    TreeSet<Booking> bookingTreeSet = new TreeSet<>(
        Comparator.comparing(Booking::getFrom));
    bookingTreeSet.add(booking);
    bookingsMap.put(booking.getInterviewerSlot().getDayOfWeek(), bookingTreeSet);
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
  @Order(0)
  void testGetInterviewerTimeSlotsByWeekId() {
    when(interviewerTimeSlotRepository.findAll()).thenReturn(List.of(interviewerSlot));
    var result =
        service.getInterviewerTimeSlotsByWeekId(WeekService.getCurrentWeekNum());
    assertEquals(interviewerTimeSlotsMap, result);
  }

  @Test
  @Order(1)
  void testGetCandidateTimeSlotsByWeekId() {
    when(candidateTimeSlotRepository.findAll()).thenReturn(List.of(candidateSlot));
    var result =
        service.getCandidateTimeSlotsByWeekId(WeekService.getCurrentWeekNum());
    assertEquals(candidateTimeSlotsMap, result);
  }

  @Test
  @Order(2)
  void testGetBookingsByWeekId() {
    when(bookingRepository.findAll()).thenReturn(List.of(booking));
    var result =
        service.getBookingsByWeekId(WeekService.getCurrentWeekNum());
    assertEquals(bookingsMap, result);
  }

  @Test
  @Order(3)
  void testGetWeekDashboard() {
    when(interviewerTimeSlotRepository.findAll()).thenReturn(List.of(interviewerSlot));
    when(candidateTimeSlotRepository.findAll()).thenReturn(List.of(candidateSlot));
    when(bookingRepository.findAll()).thenReturn(List.of(booking));

    var dashboard = service.getWeekDashboard(WeekService.getCurrentWeekNum());
    assertEquals(bookingsMap, dashboard.getBookings());
    assertEquals(candidateTimeSlotsMap, dashboard.getCandidateTimeSlots());
    assertEquals(interviewerTimeSlotsMap, dashboard.getInterviewerTimeSlots());
  }


  @Test
  void testGrantRole() {
    when(userRepository.findByEmail("interviewer@gmail.com"))
        .thenReturn(Optional.of(interviewer));
    when(userRepository.save(interviewer))
        .thenReturn(interviewer);
    var result =
        service.grantRole("interviewer@gmail.com", UserRole.INTERVIEWER);
    assertEquals(interviewer, result);
  }

  @Test
  void testGrantRoleInvalidUser() {
    when(userRepository.findByEmail("invalid@gmail.com"))
        .thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class,
        () -> service.grantRole("invalid@gmail.com", UserRole.INTERVIEWER));
  }

  @Test
  void testRevokeInterviewerRole() {
    when(userRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    when(userRepository.findByEmail("interviewer@gmail.com"))
        .thenReturn(Optional.of(new User()));

    when(userRepository.findByIdAndRole(1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.of(interviewer));
    assertEquals(UserRole.CANDIDATE,
        service.revokeInterviewerRole(1L).getRole());
  }

  @Test
  void testRevokeInterviewerRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.INTERVIEWER))
        .thenReturn(Optional.empty());
    assertThrows(InterviewerNotFoundException.class,
        () -> service.revokeInterviewerRole(-1L));
  }

  @Test
  void testRevokeCoordinatorRole() {
    when(userRepository.findByIdAndRole(1L, UserRole.COORDINATOR))
        .thenReturn(Optional.of(coordinator));
    when(userRepository.findByEmail(coordinator.getEmail()))
        .thenReturn(Optional.of(candidate));
    when(userRepository.save(any()))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    assertEquals(candidate,
        service.revokeCoordinatorRole(1L));
  }

  @Test
  void testRevokeCoordinatorRoleWrongId() {
    when(userRepository.findByIdAndRole(-1L, UserRole.COORDINATOR))
        .thenReturn(Optional.empty());
    assertThrows(CoordinatorNotFoundException.class,
        () -> service.revokeCoordinatorRole(-1L));
  }

  @Test
  void testGetUsersWithRole() {
    Set<User> set = Set.of(interviewer);
    when(userRepository.findByRole(UserRole.INTERVIEWER)).thenReturn(set);
    assertEquals(set, service.getUsersWithRole(UserRole.INTERVIEWER));
  }
}
