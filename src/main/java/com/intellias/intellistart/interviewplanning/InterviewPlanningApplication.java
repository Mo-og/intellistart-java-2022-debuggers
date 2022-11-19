package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Main application.
 */
@SpringBootApplication
@Slf4j
public class InterviewPlanningApplication {

  public static void main(String[] args) {
    SpringApplication.run(InterviewPlanningApplication.class, args);
  }

  /**
   * Inserts initial admin user to database on application startup if it is not already present in.
   */
  @Bean
  CommandLineRunner createAdmin(UserService userService, Environment env) {
    String coordinatorEmail = env.getProperty("facebook.native_user.coordinator.email");
    String interviewerEmail = env.getProperty("facebook.native_user.interviewer.email");
    return args -> {
      if (!userService.existsWithEmail(coordinatorEmail)) {
        userService.create(coordinatorEmail, UserRole.COORDINATOR);
        log.info("Initial coordinator user was created");
      } else {
        log.info("Coordinator is already present in database");
      }
      if (!userService.existsWithEmail(interviewerEmail)) {
        userService.create(interviewerEmail, UserRole.INTERVIEWER);
        log.info("Initial interviewer user was created");
      } else {
        log.info("Interviewer is already present in database");
      }
      if (Arrays.stream(env.getActiveProfiles()).anyMatch(s -> s.equalsIgnoreCase("dev"))) {
        log.debug("Users: {}", userService.getAll());
      }
    };
  }
}
