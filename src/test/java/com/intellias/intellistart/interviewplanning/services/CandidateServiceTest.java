package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.exceptions.CandidateNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.TimeSlotNotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

  private static final CandidateTimeSlot candidateSlot =
      new CandidateTimeSlot("2022-11-03", "08:00", "13:00");

  private static final User candidate = new User("test.cand@gmail.com", UserRole.CANDIDATE);

  static {
    candidate.setId(1L);
    candidateSlot.setCandidate(candidate);
  }

  @Mock
  CandidateTimeSlotRepository candidateSlotRepository;
  @Mock
  UserRepository userRepository;

  private CandidateService service;

  @BeforeEach
  void setService() {
    service = new CandidateService(candidateSlotRepository, userRepository);
  }


  @Test
  void testCreateSlot() {
    when(userRepository.getReferenceById(1L)).thenReturn(candidate);
    when(candidateSlotRepository.saveAndFlush(any())).thenAnswer(args -> args.getArgument(0));
    var slot = service.createSlot(1L, new CandidateTimeSlot());
    assertEquals(candidate, slot.getCandidate());
  }

  @Test
  void testGetSlotById() {
    when(candidateSlotRepository.getReferenceById(1L)).thenReturn(candidateSlot);
    assertEquals(candidateSlot, service.getSlot(1L));
  }

  @Test
  void testGetSlotByWrongId() {
    when(candidateSlotRepository.getReferenceById(-1L)).thenThrow(
        EntityNotFoundException.class);
    assertThrows(EntityNotFoundException.class, () -> service.getSlot(-1L));
  }

  @Test
  void testGetRelevantCandidateSlots() {
    Set<CandidateTimeSlot> set = new HashSet<>();
    set.add(candidateSlot);
    when(userRepository.existsById(1L)).thenReturn(true);
    when(candidateSlotRepository
        .getCandidateTimeSlotForCandidateIdAndWeekGreaterOrEqual(eq(1L), any()))
        .thenReturn(set);
    assertEquals(set, service.getRelevantCandidateSlots(1L));
  }

  @Test
  void testGetRelevantCandidateSlotsWrongId() {
    when(userRepository.existsById(-1L)).thenReturn(false);
    assertThrows(CandidateNotFoundException.class,
        () -> service.getRelevantCandidateSlots(-1L));
  }

  @Test
  void testUpdateSlot() {
    when(candidateSlotRepository.existsById(1L)).thenReturn(true);
    when(candidateSlotRepository.getReferenceById(1L)).thenReturn(candidateSlot);
    when(candidateSlotRepository.save(any())).thenAnswer(givenArgs -> givenArgs.getArgument(0));
    var slot = service.updateSlot(1L, candidateSlot);
    assertEquals(candidateSlot.getFrom(), slot.getFrom());
    assertEquals(candidateSlot.getTo(), slot.getTo());
    assertEquals(candidateSlot.getDate(), slot.getDate());
  }

  @Test
  void testUpdateSlotWrongId() {
    when(candidateSlotRepository.existsById(-1L)).thenReturn(false);
    assertThrows(TimeSlotNotFoundException.class,
        () -> service.updateSlot(-1L, candidateSlot));
  }

  @Test
  void testGetById() {
    when(userRepository.getReferenceById(1L)).thenReturn(candidate);
    assertEquals(candidate, service.getById(1L));
  }

  @Test
  void testGetByIdNonExisting() {
    when(userRepository.getReferenceById(-1L)).thenThrow(EntityNotFoundException.class);
    assertThrows(CandidateNotFoundException.class, () -> service.getById(-1L));
  }

}
