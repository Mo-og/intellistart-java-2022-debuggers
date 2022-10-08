package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller involved in login and user CRUD operations.
 */
@RestController
public class UserController {

  private final InterviewerService interviewerService;

  @Autowired
  public UserController(InterviewerService interviewerService) {
    this.interviewerService = interviewerService;
  }

  @GetMapping("/interviewers/{interviewerId}")
  public User getInterviewerById(@PathVariable Long interviewerId) {
    return interviewerService.getById(interviewerId);
  }
}
