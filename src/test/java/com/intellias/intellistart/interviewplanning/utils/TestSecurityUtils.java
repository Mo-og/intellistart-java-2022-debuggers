package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
public class TestSecurityUtils {

  public static final String COORDINATOR_EMAIL = "coordinator@test.com";
  public static final Long COORDINATOR_ID = 1L;
  public static final String INTERVIEWER_EMAIL = "interviewer@test.com";
  public static final Long INTERVIEWER_ID = 2L;
  public static final String CANDIDATE_EMAIL = "candidate@test.com";
  public static final Long CANDIDATE_ID = 3L;

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    final User candidate = new User(CANDIDATE_EMAIL, UserRole.CANDIDATE);
    final User coordinator = new User(COORDINATOR_EMAIL, UserRole.COORDINATOR);
    final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
    candidate.setId(CANDIDATE_ID);
    coordinator.setId(COORDINATOR_ID);
    interviewer.setId(INTERVIEWER_ID);
    //Anonymous UserDetailsService
    return username -> {
      switch (username) {
        case COORDINATOR_EMAIL:
          return coordinator;
        case INTERVIEWER_EMAIL:
          return interviewer;
        case CANDIDATE_EMAIL:
          return candidate;
        default:
          throw new UsernameNotFoundException("No user found with username " + username);
      }
    };
  }
}
