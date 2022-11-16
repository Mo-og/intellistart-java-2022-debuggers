package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityTestUtils {

  public static final String COORDINATOR_EMAIL = "coordinator@test.com";
  public static final String INTERVIEWER_EMAIL = "interviewer@test.com";
  public static final String CANDIDATE_EMAIL = "candidate@test.com";

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    User coordinator = new User(COORDINATOR_EMAIL, UserRole.COORDINATOR);
    User interviewer = new User(INTERVIEWER_EMAIL, UserRole.INTERVIEWER);
    User candidate = new User(CANDIDATE_EMAIL, UserRole.CANDIDATE);

    return new InMemoryUserDetailsManager(Arrays.asList(
        coordinator, interviewer, candidate
    ));
  }

}
