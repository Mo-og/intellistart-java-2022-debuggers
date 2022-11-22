package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.validators.PermissionValidator.checkAuthorized;

import com.intellias.intellistart.interviewplanning.controllers.dto.EmailDto;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.CoordinatorService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  public User getInterviewerById(@PathVariable Long interviewerId, Authentication auth) {
    checkAuthorized(auth, interviewerId);
    return interviewerService.getById(interviewerId);
  }

  //todo remove
  @GetMapping("/users")
  public List<User> getUser() {
    return userService.getAll();
  }

  @PostMapping("/users/interviewers")
  public User grantInterviewerRole(@RequestBody EmailDto emailDto) {
    String coordinatorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    return coordinatorService.grantInterviewerRole(emailDto.getEmail(), coordinatorEmail);
  }

  @DeleteMapping("/users/interviewers/{interviewerId}")
  public User revokeInterviewerRole(@PathVariable Long interviewerId) {
    return coordinatorService.revokeInterviewerRole(interviewerId);
  }

  @GetMapping("/users/interviewers")
  public List<User> getInterviewers() {
    return coordinatorService.getUsersWithRole(UserRole.INTERVIEWER);
  }

  @PostMapping("/users/coordinators")
  public User grantCoordinatorRole(@RequestBody EmailDto emailDto) {
    return coordinatorService.grantCoordinatorRole(emailDto.getEmail());
  }

  /**
   * Revoke the coordinator role by user id.
   *
   * @param coordinatorId id of the current coordinator
   * @return user whose coordinator role has been revoked
   */
  @DeleteMapping("/users/coordinators/{coordinatorId}")
  public User revokeCoordinatorRole(@PathVariable Long coordinatorId) {
    String coordinatorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    User coordinator = (User) userService.loadUserByUsername(coordinatorEmail);
    return coordinatorService.revokeCoordinatorRole(coordinatorId, coordinator.getId());
  }

  @GetMapping("/users/coordinators")
  public List<User> getCoordinators() {
    return coordinatorService.getUsersWithRole(UserRole.COORDINATOR);
  }
}
