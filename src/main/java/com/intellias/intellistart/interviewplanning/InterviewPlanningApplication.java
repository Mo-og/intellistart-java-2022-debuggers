package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
  CommandLineRunner createAdmin(UserService userService) {
    return args -> {
      if (!userService.existsWithEmail("mgorbiik@gmail.com")) {
        userService.create("mgorbiik@gmail.com", UserRole.CANDIDATE);
        log.info("Initial admin user was created");
      } else {
        log.info("Admin is already present in database");
      }
    };
  }
}
