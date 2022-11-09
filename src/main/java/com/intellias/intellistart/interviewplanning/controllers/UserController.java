package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
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
      System.out.println("Authentication class: " + authentication.getClass());
      System.out.println("Authentication authorities: " + authentication.getAuthorities());
      System.out.println(
          "Authentication principal class: " + authentication.getPrincipal().getClass());
    } else {
      System.out.println("Not authenticated");
    }

    return String.valueOf(authentication);
  }

  /**
   * Me endpoint. Provides current user info
   *
   * @return current user info as json object containing email and role. Also contains id if user is
   * not a Candidate
   */
  @GetMapping("/me")
  public User getUserInfo(Authentication authentication) {
    return (User) authentication.getPrincipal();
  }


  @GetMapping("/interviewers/{interviewerId}")
  public User getInterviewerById(@PathVariable Long interviewerId) {
    return interviewerService.getById(interviewerId);
  }

  //to be removed
  @GetMapping("/users/{id}")
  public User getUser(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  //to be removed
  @PostMapping("/users")
  public User postUser(@RequestBody TextNode email) {
    return userService.create(email.asText(), UserRole.CANDIDATE);
  }

  //to be removed
  @PostMapping("/interviewers")
  public User postInterviewer(@RequestBody TextNode email) {
    return userService.create(email.asText(), UserRole.INTERVIEWER);
  }

  @PostMapping("/users/interviewers")
  public User grantInterviewerRole(@RequestBody TextNode email) {
    return coordinatorService.grantRole(email.asText(), UserRole.INTERVIEWER);
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
    return coordinatorService.grantRole(email.asText(), UserRole.COORDINATOR);
  }

  @DeleteMapping("/users/coordinators/{coordinatorId}")
  public User revokeCoordinatorRole(@PathVariable Long coordinatorId) {
    return coordinatorService.revokeCoordinatorRole(coordinatorId);
  }

  @GetMapping("/users/coordinators")
  public Set<User> getCoordinators() {
    return coordinatorService.getUsersWithRole(UserRole.COORDINATOR);
  }

  @Data
  @NoArgsConstructor
  static class UserForm {

    private String email;
    private String role;

    public UserForm(User user) {
      email = user.getEmail();
      role = user.getRole().name();
    }

    public UserForm(String email, UserRole role) {
      this.email = email;
      this.role = role.name();
    }
  }
}
