package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  public static final String INTERVIEWER_EMAIL = "test.interviewer@test.com";
  private static final Interviewer newInterviewer = new Interviewer(INTERVIEWER_EMAIL);
  private static final Interviewer interviewer = new Interviewer(INTERVIEWER_EMAIL);

  static {
    interviewer.setId(1L);
  }

  @Mock
  private InterviewerTimeSlotRepository mockedInterviewerTimeSlotRepository;
  @Mock
  private InterviewerRepository mockedInterviewerRepository;
  private InterviewerService service;

  @BeforeEach
  void setService() {
    service = new InterviewerService(mockedInterviewerTimeSlotRepository,
        mockedInterviewerRepository);
  }

/*  @Test
  void testCreateCandidateByEmail() {
    when(mockedCandidateRepository
        .save(newCandidate))
        .thenReturn(candidate);
    var createdCandidate = service.create(CANDIDATE_EMAIL);
    assertEquals(candidate.getId(), createdCandidate.getId());
    assertEquals(candidate.getUserRole(), createdCandidate.getUserRole());
    assertEquals(candidate.getEmail(), createdCandidate.getEmail());
  }*/
}
