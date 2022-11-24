package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.controllers.dto.EmailDto;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing users.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final CoordinatorService coordinatorService;

  /**
   * Endpoint to provide current user info.
   *
   * @param auth object from spring security containing the principle presented by our user
   * @return current user info as json object containing full name, email and role
   */
  @GetMapping("/me")
  public User getUserInfo(Authentication auth) {
    return (User) auth.getPrincipal();
  }

  /**
   * Endpoint to grant user the interviewer role.
   *
   * @param emailDto user email
   * @param auth     object from spring security containing the principle presented by our user
   * @return user with the granted interviewer role
   */
  @PostMapping("/users/interviewers")
  public User grantInterviewerRole(@RequestBody EmailDto emailDto, Authentication auth) {
    User authenticatedUser = (User) auth.getPrincipal();
    return coordinatorService
        .grantInterviewerRole(emailDto.getEmail(), authenticatedUser.getEmail());
  }

  /**
   * Endpoint to revoke user the interviewer role.
   *
   * @param interviewerId id of the interviewer
   * @return user with revoked interviewer role
   */
  @DeleteMapping("/users/interviewers/{interviewerId}")
  public User revokeInterviewerRole(@PathVariable Long interviewerId) {
    return coordinatorService.revokeInterviewerRole(interviewerId);
  }

  /**
   * Endpoint to get users with the interviewer role.
   *
   * @return users with the interviewer role
   */
  @GetMapping("/users/interviewers")
  public List<User> getInterviewers() {
    return coordinatorService.getUsersWithRole(UserRole.INTERVIEWER);
  }

  /**
   * Endpoint to grant user the coordinator role.
   *
   * @param emailDto user email
   * @return user with the granted coordinator role
   */
  @PostMapping("/users/coordinators")
  public User grantCoordinatorRole(@RequestBody EmailDto emailDto) {
    return coordinatorService.grantCoordinatorRole(emailDto.getEmail());
  }

  /**
   * Endpoint to revoke user the coordinator role.
   *
   * @param coordinatorId id of the coordinator
   * @param auth          object from spring security containing the principle presented by user
   * @return user with revoked coordinator role
   */
  @DeleteMapping("/users/coordinators/{coordinatorId}")
  public User revokeCoordinatorRole(@PathVariable Long coordinatorId, Authentication auth) {
    User authenticatedUser = (User) auth.getPrincipal();
    return coordinatorService.revokeCoordinatorRole(coordinatorId, authenticatedUser.getId());
  }

  /**
   * Endpoint to get users with the coordinator role.
   *
   * @return users with the coordinator role
   */
  @GetMapping("/users/coordinators")
  public List<User> getCoordinators() {
    return coordinatorService.getUsersWithRole(UserRole.COORDINATOR);
  }
}
