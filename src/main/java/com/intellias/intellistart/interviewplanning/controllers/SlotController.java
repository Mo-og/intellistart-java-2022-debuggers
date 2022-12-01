package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.validators.PermissionValidator.checkAuthorized;
import static com.intellias.intellistart.interviewplanning.validators.PermissionValidator.getUserEmail;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.controllers.dto.InterviewerSlotDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for resolving slot-related requests.
 */
@RestController
@RequiredArgsConstructor
public class SlotController {

  private final InterviewerService interviewerService;
  private final CandidateService candidateService;
  private final WeekService weekService;

  /**
   * Checks if user is allowed to view current interviewer slots and returns a list of them if so.
   */
  @GetMapping("/interviewers/{interviewerId}/slots")
  //todo replace with DTO that has list as field
  public List<InterviewerSlotDto> getAllInterviewerSlots(
      @PathVariable Long interviewerId,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.getRelevantInterviewerSlots(interviewerId);
  }

  /**
   * Adds slot to interviewer if requested id is theirs, or adds regardless of id if requester
   * authorized as COORDINATOR. Otherwise, throws exception giving 403 code.
   */
  @PostMapping("/interviewers/{interviewerId}/slots")
  public InterviewerSlotDto addSlotToInterviewer(
      @PathVariable Long interviewerId,
      @RequestBody InterviewerSlotDto interviewerSlotDto,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.createSlot(interviewerId, interviewerSlotDto);
  }

  /**
   * Adds slot to candidate.
   *
   * @param candidateSlotDto candidate slot information
   * @param auth             object from spring security containing the principle presented by user
   * @return created candidate slot
   */
  @PostMapping("/candidates/current/slots")
  public CandidateSlotDto addSlotToCandidate(
      @RequestBody CandidateSlotDto candidateSlotDto,
      @RequestParam(required = false) String email,
      Authentication auth) {

    String candidateEmail = getUserEmail(auth, email);
    return candidateService.createSlot(candidateEmail, candidateSlotDto);
  }

  /**
   * Updates slot of interviewer if requested id is theirs, or updates regardless of id if requester
   * authorized as COORDINATOR. Otherwise, throws exception giving 403 code.
   */
  @PostMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public InterviewerSlotDto updateInterviewerTimeSlot(
      @PathVariable Long interviewerId,
      @PathVariable Long slotId,
      @RequestBody InterviewerSlotDto interviewerSlotDto,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.updateSlot(interviewerId, slotId, interviewerSlotDto);
  }

  /**
   * Checks if requester has same id as in request or if requester is a COORDINATOR and then deletes
   * slot. Otherwise, throws exception giving 403 code.
   */
  @DeleteMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public InterviewerSlotDto deleteInterviewerTimeSlot(
      @PathVariable Long interviewerId,
      @PathVariable Long slotId,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.deleteSlot(interviewerId, slotId, (User) auth.getPrincipal());
  }

  /**
   * Adds a slot to candidate if the requester a candidate themselves or uses 'email' request param
   * if the requester is COORDINATOR.
   */
  @PostMapping("/candidates/current/slots/{slotId}")
  public CandidateSlotDto updateCandidateTimeSlot(
      @PathVariable Long slotId,
      @RequestBody CandidateSlotDto candidateSlotDto,
      @RequestParam(required = false) String email,
      Authentication auth) {

    String candidateEmail = getUserEmail(auth, email);
    return candidateService.updateSlot(candidateEmail, slotId, candidateSlotDto);
  }

  /**
   * Endpoint to get all candidate time slots.
   *
   * @param auth object from spring security containing the principle presented by user
   * @return candidate time slots
   */
  @GetMapping("/candidates/current/slots")
  public List<CandidateSlotDto> getAllCandidateSlots(
      @RequestParam(required = false) String email,
      Authentication auth) {

    String candidateEmail = getUserEmail(auth, email);
    return candidateService.getAllCandidateSlots(candidateEmail);
  }

  /**
   * Returns a list of current week slots if id is same as of authorized user or authorized user is
   * coordinator.
   */
  @GetMapping("/interviewers/{interviewerId}/slots/weeks/current")
  //todo replace with DTO that has list as field
  public List<InterviewerSlotDto> getCurrentWeekInterviewerSlots(
      @PathVariable Long interviewerId,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.getSlotsByWeekId(interviewerId, weekService.getCurrentWeekNum());
  }

  /**
   * Returns a list of next week slots if id is same as of authorized user or authorized user is
   * coordinator.
   */
  @GetMapping("/interviewers/{interviewerId}/slots/weeks/next")
  //todo replace with DTO that has list as field
  public List<InterviewerSlotDto> getNextWeekInterviewerSlots(
      @PathVariable Long interviewerId,
      Authentication auth) {

    checkAuthorized(auth, interviewerId);
    return interviewerService.getSlotsByWeekId(interviewerId, weekService.getNextWeekNum());
  }
}
