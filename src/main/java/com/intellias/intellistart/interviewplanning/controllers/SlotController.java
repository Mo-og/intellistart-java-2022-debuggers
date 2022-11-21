package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.services.CandidateService;
import com.intellias.intellistart.interviewplanning.services.InterviewerService;
import com.intellias.intellistart.interviewplanning.services.interfaces.WeekService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller resolving slot-related requests.
 */
@RestController
@RequiredArgsConstructor
public class SlotController {

  private final InterviewerService interviewerService;
  private final CandidateService candidateService;
  private final WeekService weekService;


  @GetMapping("/interviewers/{interviewerId}/slots")
  public List<InterviewerSlotDto> getAllInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getRelevantInterviewerSlots(interviewerId);
  }

  @PostMapping("/interviewers/{interviewerId}/slots")
  public InterviewerSlotDto addSlotToInterviewer(
      @RequestBody InterviewerSlotDto interviewerSlotDto,
      @PathVariable Long interviewerId) {
    return interviewerService.createSlot(interviewerId, interviewerSlotDto);
  }

  @PostMapping("/candidates/current/slots")
  public CandidateSlotDto addSlotToCandidate(
      Authentication authentication, @RequestBody CandidateSlotDto candidateSlotDto) {
    return candidateService.createSlot(((User) authentication.getPrincipal()).getEmail(),
        candidateSlotDto);
  }

  @PostMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public InterviewerSlotDto updateInterviewerTimeSlot(@PathVariable Long interviewerId,
      @PathVariable long slotId, @RequestBody InterviewerSlotDto interviewerSlotDto) {
    return interviewerService.updateSlot(interviewerId, slotId, interviewerSlotDto);
  }

  @DeleteMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public InterviewerSlotDto deleteInterviewerTimeSlot(@PathVariable Long interviewerId,
      @PathVariable long slotId, Authentication auth) {
    User user = (User) auth.getPrincipal();
    return interviewerService.deleteSlot(interviewerId, slotId, user);
  }

  @PostMapping("/candidates/current/slots/{slotId}")
  public CandidateSlotDto updateCandidateTimeSlot(Authentication authentication,
      @PathVariable Long slotId,
      @RequestBody CandidateSlotDto candidateSlotDto) {
    return candidateService.updateSlot(((User) authentication.getPrincipal()).getEmail(),
        slotId, candidateSlotDto);
  }

  @GetMapping("/candidates/current/slots")
  public List<CandidateSlotDto> checkAllCandidateSlots(
      Authentication authentication) {
    return candidateService.getAllCandidateSlots(((User) authentication.getPrincipal()).getEmail());
  }

  @GetMapping("/interviewers/{interviewerId}/slots/weeks/current")
  public List<InterviewerSlotDto> getCurrentWeekInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getSlotsByWeekId(interviewerId, weekService.getCurrentWeekNum());
  }

  @GetMapping("/interviewers/{interviewerId}/slots/weeks/next")
  public List<InterviewerSlotDto> getNextWeekInterviewerSlots(@PathVariable Long interviewerId) {
    return interviewerService.getSlotsByWeekId(interviewerId, weekService.getNextWeekNum());
  }
}
