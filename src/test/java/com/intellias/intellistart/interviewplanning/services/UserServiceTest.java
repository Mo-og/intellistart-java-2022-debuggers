package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.CoordinatorNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Candidate;
import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.CandidateRepository;
import com.intellias.intellistart.interviewplanning.repositories.CoordinatorRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  public static final String CANDIDATE_EMAIL = "test.candidate@test.com";
  public static final String INTERVIEWER_EMAIL = "test.interviewer@test.com";
  public static final String COORDINATOR_EMAIL = "test.coordinator@test.com";
  private static final Candidate newCandidate = new Candidate(CANDIDATE_EMAIL);
  private static final Candidate candidate = new Candidate(CANDIDATE_EMAIL);
  private static final Interviewer newInterviewer = new Interviewer(INTERVIEWER_EMAIL);
  private static final Interviewer interviewer = new Interviewer(INTERVIEWER_EMAIL);
  private static final User newCoordinator = new User(COORDINATOR_EMAIL,
      UserRole.COORDINATOR);
  private static final User coordinator = new User(COORDINATOR_EMAIL,
      UserRole.COORDINATOR);

  static {
    candidate.setId(1L);
    interviewer.setId(1L);
    coordinator.setId(1L);
  }

  @Mock
  CoordinatorRepository mockedCoordinatorRepository;
  @Mock
  InterviewerRepository mockedInterviewerRepository;
  @Mock
  CandidateRepository mockedCandidateRepository;
  private UserService service;

  @BeforeEach
  void setService() {
    service = new UserService(mockedCoordinatorRepository, mockedInterviewerRepository,
        mockedCandidateRepository);
  }

  @Test
  void testCreateCandidateByEmail() {
    when(mockedCandidateRepository
        .save(newCandidate))
        .thenReturn(candidate);
    var createdCandidate = service.create(CANDIDATE_EMAIL);
    assertEquals(candidate.getId(), createdCandidate.getId());
    assertEquals(candidate.getUserRole(), createdCandidate.getUserRole());
    assertEquals(candidate.getEmail(), createdCandidate.getEmail());
  }

  @Test
  void testCreateCandidateByRole() {
    when(mockedCandidateRepository
        .save(newCandidate))
        .thenReturn(candidate);
    var createdCandidate = service.create(CANDIDATE_EMAIL, UserRole.CANDIDATE);
    assertEquals(candidate.getId(), createdCandidate.getId());
    assertEquals(candidate.getUserRole(), createdCandidate.getUserRole());
    assertEquals(candidate.getEmail(), createdCandidate.getEmail());
  }

  @Test
  void testCreateInterviewerByRole() {
    when(mockedInterviewerRepository
        .save(newInterviewer))
        .thenReturn(interviewer);
    var savedInterviewer = service.create(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
    assertEquals(interviewer.getId(), savedInterviewer.getId());
    assertEquals(interviewer.getUserRole(), savedInterviewer.getUserRole());
    assertEquals(interviewer.getEmail(), savedInterviewer.getEmail());
  }

  @Test
  void testCreateCoordinatorByRole() {
    when(mockedCoordinatorRepository
        .save(newCoordinator))
        .thenReturn(coordinator);
    var savedCoordinator = service.create(COORDINATOR_EMAIL, UserRole.COORDINATOR);
    assertEquals(coordinator.getId(), savedCoordinator.getId());
    assertEquals(coordinator.getUserRole(), savedCoordinator.getUserRole());
    assertEquals(coordinator.getEmail(), savedCoordinator.getEmail());
  }

  @Test
  void testSaveCandidateCorrectly() {
    when(mockedCandidateRepository
        .save(candidate))
        .thenReturn(candidate);
    var savedCandidate = service.save(candidate);
    assertEquals(candidate.getId(), savedCandidate.getId());
    assertEquals(candidate.getUserRole(), savedCandidate.getUserRole());
    assertEquals(candidate.getEmail(), savedCandidate.getEmail());
  }

  @Test
  void testSaveInterviewerCorrectly() {
    when(mockedInterviewerRepository
        .save(interviewer))
        .thenReturn(interviewer);
    var savedInterviewer = service.save(interviewer);
    assertEquals(interviewer.getId(), savedInterviewer.getId());
    assertEquals(interviewer.getUserRole(), savedInterviewer.getUserRole());
    assertEquals(interviewer.getEmail(), savedInterviewer.getEmail());
  }

  @Test
  void testSaveCoordinatorCorrectly() {
    when(mockedCoordinatorRepository
        .save(coordinator))
        .thenReturn(coordinator);
    var savedCoordinator = service.save(coordinator);
    assertEquals(coordinator.getId(), savedCoordinator.getId());
    assertEquals(coordinator.getUserRole(), savedCoordinator.getUserRole());
    assertEquals(coordinator.getEmail(), savedCoordinator.getEmail());
  }

  @Test
  void testGetById() {
    when(mockedCoordinatorRepository
        .getReferenceById(1L))
        .thenReturn(coordinator);
    var coordinatorById = service.getCoordinatorById(1L);
    assertEquals(coordinator.getId(), coordinatorById.getId());
    assertEquals(coordinator.getUserRole(), coordinatorById.getUserRole());
    assertEquals(coordinator.getEmail(), coordinatorById.getEmail());
  }

  @Test
  void testGetByWrongId() {
    when(mockedCoordinatorRepository
        .getReferenceById(-1L))
        .thenThrow(new EntityNotFoundException());
    assertThrows(CoordinatorNotFoundException.class, () -> service.getCoordinatorById(-1L));
  }

  @Test
  void testRemoveByWrongId() {
    doThrow(new EntityNotFoundException()).when(mockedCoordinatorRepository).deleteById(-1L);
    assertThrows(CoordinatorNotFoundException.class, () -> service.removeCoordinatorById(-1L));
  }

  @Test
  void testRemoveById() {
    doNothing().when(mockedCoordinatorRepository).deleteById(1L);
    assertDoesNotThrow(() -> service.removeCoordinatorById(1L));
  }
}
