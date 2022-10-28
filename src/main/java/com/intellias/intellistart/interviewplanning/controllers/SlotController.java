package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller resolving slot-related requests.
 */
@RestController
public class SlotController {

  private final InterviewerService interviewerService;

  private final CandidateService candidateService;

  @Autowired
  public SlotController(InterviewerService interviewerService, CandidateService candidateService) {
    this.interviewerService = interviewerService;
    this.candidateService = candidateService;
  }

  @GetMapping("/interviewers/{interviewerId}/slots")
  public Set<InterviewerTimeSlot> getAllInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getRelevantInterviewerSlots(interviewerId);
  }

  @GetMapping("/candidate/{candidateId}/slots")
  public Set<CandidateTimeSlot> getAllCandidateSlots(@PathVariable Long candidateId) {
    return candidateService.getRelevantCandidateSlots(candidateId);
  }

  @PostMapping("/interviewers/{interviewerId}/slots")
  public InterviewerTimeSlot addSlotToInterviewer(
      @RequestBody InterviewerTimeSlot interviewerTimeSlot,
      @PathVariable Long interviewerId) {
    return interviewerService.createSlot(interviewerId, interviewerTimeSlot);
  }

  @PostMapping("/candidate/{candidateId}/slots")
  public CandidateTimeSlot addSlotToCandidate(
      @RequestBody CandidateTimeSlot candidateTimeSlot,
      @PathVariable Long candidateId) {
    return candidateService.createSlot(candidateId, candidateTimeSlot);
  }
}
