package com.intellias.intellistart.interviewplanning.services;

import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.COORDINATOR_EMAIL;
import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.INTERVIEWER_EMAIL;
import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.coordinator;
import static com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils.interviewer;
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
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final User newInterviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final User newCoordinator = new User(COORDINATOR_EMAIL, UserRole.COORDINATOR);

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
    var savedInterviewer = service.create(INTERVIEWER_EMAIL,
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

  @Test
  void testGetByEmail() {
    String existEmail = "test@test.ua";
    when(userRepository.findByEmail(existEmail))
        .thenReturn(Optional.of(interviewer));
    var savedInterviewer = service.getByEmail(existEmail);
    assertEquals(interviewer.getId(), savedInterviewer.getId());
    assertEquals(interviewer.getRole(), savedInterviewer.getRole());
    assertEquals(interviewer.getEmail(), savedInterviewer.getEmail());
  }

  @Test
  void testThrowExceptionGetByEmail() {
    String unExistEmail = "wrong@email.ua";
    when(userRepository.findByEmail(unExistEmail))
        .thenThrow(NotFoundException.user(unExistEmail));
    assertThrows(NotFoundException.class, () -> service.getByEmail(unExistEmail));
  }

  @Test
  void testLoadUserByUsername() {
    when(userRepository.findByEmail(INTERVIEWER_EMAIL))
        .thenReturn(Optional.of(newInterviewer));
    var savedInterviewer = service.loadUserByUsername(INTERVIEWER_EMAIL);
    assertEquals(INTERVIEWER_EMAIL, savedInterviewer.getUsername());
    assertEquals(newInterviewer.getRole(), ((User) savedInterviewer).getRole());
    assertEquals(newInterviewer.getId(), ((User) savedInterviewer).getId());
    assertEquals(newInterviewer.getEmail(), ((User) savedInterviewer).getEmail());
  }

  @Test
  void testThrowExceptionLoadByUsername() {
    when(userRepository.findByEmail(INTERVIEWER_EMAIL))
        .thenThrow(new UsernameNotFoundException(
            "No user found with username " + INTERVIEWER_EMAIL));
    assertThrows(UsernameNotFoundException.class,
        () -> service.loadUserByUsername(INTERVIEWER_EMAIL));
  }

  @Test
  void testGetAllUsers() {
    when(userRepository.findAll())
        .thenReturn(List.of(interviewer, coordinator, newInterviewer, newCoordinator));
    var result = service.getAll();
    assertEquals(List.of(interviewer, coordinator, newInterviewer, newCoordinator), result);
  }

  @Test
  void testThrowExceptionLoadUserByUsername() {
    when(userRepository.findByEmail(INTERVIEWER_EMAIL))
        .thenThrow(new UsernameNotFoundException(
            "No user found with username " + INTERVIEWER_EMAIL));
    assertThrows(UsernameNotFoundException.class,
        () -> service.loadUserByUsername(INTERVIEWER_EMAIL));
  }
}