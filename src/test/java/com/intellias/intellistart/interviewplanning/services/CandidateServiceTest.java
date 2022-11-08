package com.intellias.intellistart.interviewplanning.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.intellias.intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.BookingRepository;
import com.intellias.intellistart.interviewplanning.repositories.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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
  private static final CandidateSlotDto candidateSlotDto =
      CandidateSlotDto.builder()
          .date(LocalDate.of(2022, 11, 3))
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(13, 0))
          .build();
  private static final CandidateSlotDto candidateSlotDtoWithBookings =
      CandidateSlotDto.builder()
          .date(LocalDate.of(2022, 11, 3))
          .from(LocalTime.of(8, 0))
          .to(LocalTime.of(13, 0))
          .build();
  private static final User candidate = new User("test.cand@gmail.com", UserRole.CANDIDATE);

  static {
    candidate.setId(1L);
    candidateSlot.setCandidate(candidate);
    candidateSlotDtoWithBookings.setBookings(Collections.emptySet());
  }

  @Mock
  CandidateTimeSlotRepository candidateSlotRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  BookingRepository bookingRepository;

  private CandidateService service;

  @BeforeEach
  void setService() {
    service = new CandidateService(candidateSlotRepository, userRepository, bookingRepository);
  }

  @Test
  void testCreateSlot() {
    when(userRepository.getReferenceById(1L)).thenReturn(candidate);
    when(candidateSlotRepository.save(candidateSlot)).thenReturn(candidateSlot);

    var slot = service.createSlot(1L, candidateSlotDto);
    assertEquals(candidateSlotDto, slot);
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
  void testAllCandidateSlots() {
    when(userRepository.existsByIdAndRole(1L, UserRole.CANDIDATE)).thenReturn(true);
    when(candidateSlotRepository.findAll()).thenReturn(List.of(candidateSlot));
    assertEquals(Set.of(candidateSlotDtoWithBookings), service.getAllCandidateSlots(1L));
  }

  @Test
  void testGetCandidateSlotsWithBookings() {
    var result = service.getCandidateSlotsWithBookings(List.of(candidateSlot));
    assertEquals(Set.of(candidateSlotDtoWithBookings), result);
  }

  @Test
  void testGetAllCandidateSlotsWrongId() {
    when(userRepository.existsByIdAndRole(-1L, UserRole.CANDIDATE)).thenReturn(false);
    assertThrows(NotFoundException.class,
        () -> service.getAllCandidateSlots(-1L));
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
    assertThrows(NotFoundException.class,
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
    assertThrows(NotFoundException.class, () -> service.getById(-1L));
  }
}