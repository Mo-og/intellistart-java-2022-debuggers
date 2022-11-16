package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  public static final String COORDINATOR_EMAIL = "test.coordinator@test.com";
  private static final User newInterviewer = new User(
      InterviewerServiceTest.INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final User interviewer = new User(
      InterviewerServiceTest.INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final User newCoordinator = new User(COORDINATOR_EMAIL,
      UserRole.COORDINATOR);
  private static final User coordinator = new User(COORDINATOR_EMAIL,
      UserRole.COORDINATOR);

  static {
    interviewer.setId(1L);
    coordinator.setId(1L);
  }

  @Mock
  UserRepository userRepository;
  private UserService service;

  @BeforeEach
  void setService() {
    service = new UserService(userRepository);
  }

  @Test
  void testCreateInterviewerByRole() {
    when(userRepository
        .save(newInterviewer))
        .thenReturn(interviewer);
    var savedInterviewer = service.create(InterviewerServiceTest.INTERVIEWER_EMAIL,
        UserRole.INTERVIEWER);
    assertEquals(interviewer.getId(), savedInterviewer.getId());
    assertEquals(interviewer.getRole(), savedInterviewer.getRole());
    assertEquals(interviewer.getEmail(), savedInterviewer.getEmail());
  }

  @Test
  void testCreateCoordinatorByRole() {
    when(userRepository
        .save(newCoordinator))
        .thenReturn(coordinator);
    var savedCoordinator = service.create(COORDINATOR_EMAIL, UserRole.COORDINATOR);
    assertEquals(coordinator.getId(), savedCoordinator.getId());
    assertEquals(coordinator.getRole(), savedCoordinator.getRole());
    assertEquals(coordinator.getEmail(), savedCoordinator.getEmail());
  }

  @Test
  void testSaveInterviewerCorrectly() {
    when(userRepository
        .save(interviewer))
        .thenReturn(interviewer);
    var savedInterviewer = service.save(interviewer);
    assertEquals(interviewer.getId(), savedInterviewer.getId());
    assertEquals(interviewer.getRole(), savedInterviewer.getRole());
    assertEquals(interviewer.getEmail(), savedInterviewer.getEmail());
  }

  @Test
  void testSaveCoordinatorCorrectly() {
    when(userRepository
        .save(coordinator))
        .thenReturn(coordinator);
    var savedCoordinator = service.save(coordinator);
    assertEquals(coordinator.getId(), savedCoordinator.getId());
    assertEquals(coordinator.getRole(), savedCoordinator.getRole());
    assertEquals(coordinator.getEmail(), savedCoordinator.getEmail());
  }

  @Test
  void testGetById() {
    when(userRepository
        .getReferenceById(1L))
        .thenReturn(coordinator);
    var coordinatorById = service.getById(1L);
    assertEquals(coordinator.getId(), coordinatorById.getId());
    assertEquals(coordinator.getRole(), coordinatorById.getRole());
    assertEquals(coordinator.getEmail(), coordinatorById.getEmail());
  }

  @Test
  void testGetByWrongId() {
    when(userRepository
        .getReferenceById(-1L))
        .thenThrow(new EntityNotFoundException());
    assertThrows(NotFoundException.class, () -> service.getById(-1L));
  }

  @Test
  void testRemoveByWrongId() {
    doThrow(new EntityNotFoundException()).when(userRepository).deleteById(-1L);
    assertThrows(NotFoundException.class, () -> service.removeById(-1L));
  }

  @Test
  void testRemoveById() {
    doNothing().when(userRepository).deleteById(1L);
    assertDoesNotThrow(() -> service.removeById(1L));
  }
}
