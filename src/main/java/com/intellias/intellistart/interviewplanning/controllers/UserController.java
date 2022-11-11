package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.List;
import java.util.Set;
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
 * Controller involved in login and user CRUD operations.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final InterviewerService interviewerService;
  private final CoordinatorService coordinatorService;
  private final UserService userService;


  /**
   * Test method to see what token contains.
   *
   * @param authentication Object from spring security containing the principle presented by our
   *                       user.
   * @return toString() of received authentication object
   */
  @GetMapping("/")
  //todo remove
  public String test(Authentication authentication) {
    if (authentication != null) {
      log.debug("Authentication class: " + authentication.getClass());
      log.debug("Authentication authorities: " + authentication.getAuthorities());
      log.debug("Authentication principal class: {}", authentication.getPrincipal().getClass());
    } else {
      log.debug("Not authenticated");
    }
    return String.valueOf(authentication);
  }

  /**
   * Me endpoint. Provides current user info
   *
   * @return current user info as json object containing email and role
   */
  @GetMapping("/me")
  public User getUserInfo(Authentication authentication) {
    return (User) authentication.getPrincipal();
  }


  @GetMapping("/interviewers/{interviewerId}")
  public User getInterviewerById(@PathVariable Long interviewerId) {
    return interviewerService.getById(interviewerId);
  }

  //todo remove
  @GetMapping("/users/{id}")
  public User getUser(@PathVariable Long id) {
    return userService.getById(id);
  }

  //todo remove
  @GetMapping("/users")
  public List<User> getUser() {
    return userService.getAll();
  }

  //todo remove
  @PostMapping("/interviewers")
  public User postInterviewer(@RequestBody TextNode email) {
    return userService.create(email.asText(), UserRole.INTERVIEWER);
  }

  @PostMapping("/users/interviewers")
  public User grantInterviewerRole(@RequestBody TextNode email) {
    return coordinatorService.grantInterviewerRole(email.asText());
  }

  @DeleteMapping("/users/interviewers/{interviewerId}")
  public User revokeInterviewerRole(@PathVariable Long interviewerId) {
    return coordinatorService.revokeInterviewerRole(interviewerId);
  }

  @GetMapping("/users/interviewers")
  public Set<User> getInterviewers() {
    return coordinatorService.getUsersWithRole(UserRole.INTERVIEWER);
  }

  @PostMapping("/users/coordinators")
  public User grantCoordinatorRole(@RequestBody TextNode email) {
    return coordinatorService.grantCoordinatorRole(email.asText());
  }

  @DeleteMapping("/users/coordinators/{coordinatorId}")
  public User revokeCoordinatorRole(@PathVariable Long coordinatorId) {
    return coordinatorService.revokeCoordinatorRole(coordinatorId);
  }

  @GetMapping("/users/coordinators")
  public Set<User> getCoordinators() {
    return coordinatorService.getUsersWithRole(UserRole.COORDINATOR);
  }
}
