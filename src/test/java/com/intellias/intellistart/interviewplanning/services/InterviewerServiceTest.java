package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
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
  private static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final InterviewerTimeSlot timeSlot = new InterviewerTimeSlot("09:00",
      "18:00", "Mon", 202210);
  private static final InterviewerTimeSlot timeSlotWithUser = new InterviewerTimeSlot(
      "09:00",
      "18:00", "Mon", 202210);

  static {
    interviewer.setId(1L);
    timeSlotWithUser.setId(1L);
    timeSlotWithUser.setInterviewer(interviewer);
  }

  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private UserRepository userRepository;
  private InterviewerService interviewerService;

  @BeforeEach
  void setService() {
    interviewerService = new InterviewerService(interviewerTimeSlotRepository,
        userRepository);
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
    var retrievedSet = interviewerService.getRelevantInterviewerSlots(1L);
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
    assertThrows(NotFoundException.class, () -> interviewerService.getById(-1L));
  }


}
