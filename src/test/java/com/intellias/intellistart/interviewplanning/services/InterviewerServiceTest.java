package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

  public static final String INTERVIEWER_EMAIL = "test.interviewer@test.com";
  private static final User newInterviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
  private static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);

  static {
    interviewer.setId(1L);
  }

  @Mock
  private InterviewerTimeSlotRepository mockedInterviewerTimeSlotRepository;
  @Mock
  private UserRepository userRepository;
  private InterviewerService service;

  @BeforeEach
  void setService() {
    service = new InterviewerService(mockedInterviewerTimeSlotRepository,
        userRepository);
  }
}
