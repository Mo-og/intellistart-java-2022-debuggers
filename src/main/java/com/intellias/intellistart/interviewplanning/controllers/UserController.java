package com.intellias.intellistart.interviewplanning.controllers;

import com.fasterxml.jackson.databind.node.TextNode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller involved in login and user CRUD operations.
 */
@RestController
@Slf4j
public class UserController {

  private final InterviewerService interviewerService;
  private final UserService userService;

  @Autowired
  public UserController(InterviewerService interviewerService, UserService userService) {
    this.interviewerService = interviewerService;
    this.userService = userService;
  }

  @GetMapping("/interviewers/{interviewerId}")
  public User getInterviewerById(@PathVariable Long interviewerId) {
    return interviewerService.getById(interviewerId);
  }

  //to be removed
  @GetMapping("/users/{id}")
  public User getUser(@PathVariable Long id) {
    return userService.getById(id);
  }

  //to be removed
  @PostMapping("/users")
  public User postUser(@RequestBody TextNode email) {
    return userService.create(email.asText());
  }

  //to be removed (?)
  @PostMapping("/interviewers")
  public User postInterviewer(@RequestBody TextNode email) {
    return userService.create(email.asText(), UserRole.INTERVIEWER);
  }
}
