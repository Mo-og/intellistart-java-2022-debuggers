package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.WeekService;
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

  @PostMapping("/interviewers/{interviewerId}/slots")
  public InterviewerTimeSlot addSlotToInterviewer(
      @RequestBody InterviewerTimeSlot interviewerTimeSlot,
      @PathVariable Long interviewerId) {
    return interviewerService.createSlot(interviewerId, interviewerTimeSlot);
  }

  @PostMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public InterviewerTimeSlot updateInterviewerTimeSlot(@PathVariable Long interviewerId,
      @PathVariable long slotId, @RequestBody InterviewerTimeSlot interviewerTimeSlot) {
    return interviewerService.updateSlot(interviewerId, slotId, interviewerTimeSlot);
  }

  @PostMapping("/candidates/current/slots")
  public CandidateTimeSlot createCandidateTimeSlot(@PathVariable Long candidateId,
      @RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.createSlot(candidateId, candidateTimeSlot);
  }

  @PostMapping("/candidates/current/slots/{slotId}")
  public CandidateTimeSlot updateCandidateTimeSlot(@PathVariable Long slotId,
      @RequestBody CandidateTimeSlot candidateTimeSlot) {
    return candidateService.updateSlot(slotId, candidateTimeSlot);
  }

  @GetMapping("/candidates/current/slots")
  public Set<CandidateTimeSlot> getListOfAllCandidateSlots(@PathVariable Long candidateId) {
    return candidateService.getRelevantCandidateSlots(candidateId);
  }

  @GetMapping("/interviewers/{interviewerId}/slots/weeks/current")
  public Set<InterviewerTimeSlot> getCurrentWeekInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getSlotsByWeekId(interviewerId, WeekService.getCurrentWeekNum());
  }

  @GetMapping("/interviewers/{interviewerId}/slots/weeks/next")
  public Set<InterviewerTimeSlot> getNextWeekInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getSlotsByWeekId(interviewerId, WeekService.getNextWeekNum());
  }
}
