package com.intellias.intellistart.interviewplanning.test_utils;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
@Slf4j
public class TestSecurityUtils {

  public static final String COORDINATOR_EMAIL = "coordinator@test.com";
  public static final Long COORDINATOR_ID = 1L;
  public static final String INTERVIEWER_EMAIL = "interviewer@test.com";
  public static final Long INTERVIEWER_ID = 2L;
  public static final String CANDIDATE_EMAIL = "candidate@test.com";
  public static final Long CANDIDATE_ID = 3L;
  public static final User candidate = new User(CANDIDATE_EMAIL, UserRole.CANDIDATE).setId(CANDIDATE_ID);
  public static final User coordinator = new User(COORDINATOR_EMAIL, UserRole.COORDINATOR).setId(COORDINATOR_ID);
  public static final User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER).setId(INTERVIEWER_ID);

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {

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

  @Bean
  @Primary
  CommandLineRunner createTestUsers(UserService userService, Environment env) {
    return args -> {
      if (!userService.existsWithEmail(COORDINATOR_EMAIL)) {
        userService.create(COORDINATOR_EMAIL, UserRole.COORDINATOR);
      }
      if (!userService.existsWithEmail(INTERVIEWER_EMAIL)) {
        userService.create(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
      }
      if (Arrays.stream(env.getActiveProfiles()).anyMatch(s -> s.equalsIgnoreCase("dev"))) {
        log.debug("Users: {}", userService.getAll());
      }
    };
  }
}
