package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.CoordinatorNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Candidate;
import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.CandidateRepository;
import com.intellias.intellistart.interviewplanning.repositories.CoordinatorRepository;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {

  private final CoordinatorRepository coordinatorRepository;
  private final InterviewerRepository interviewerRepository;
  private final CandidateRepository candidateRepository;

  /**
   * Main constructor.
   *
   * @param coordinatorRepository coordinatorRepository to inject into service
   * @param interviewerRepository interviewerRepository to inject into service
   * @param candidateRepository   candidateRepository to inject into service
   */
  @Autowired
  public UserService(CoordinatorRepository coordinatorRepository,
      InterviewerRepository interviewerRepository,
      CandidateRepository candidateRepository) {
    this.coordinatorRepository = coordinatorRepository;
    this.interviewerRepository = interviewerRepository;
    this.candidateRepository = candidateRepository;
  }

  /**
   * Creates user and saves it to database.
   *
   * @param email user email
   * @param role  user role according to which permissions will be granted
   * @return user with generated id
   */
  public User create(String email, UserRole role) {
    switch (role) {
      case INTERVIEWER:
        return interviewerRepository.save(new Interviewer(email));
      case COORDINATOR:
        return coordinatorRepository.save(new User(email, role));
      case CANDIDATE:
        return candidateRepository.save(new Candidate(email));
      default:
        throw new IllegalArgumentException("Illegal role");
    }
  }

  public Candidate create(String email) {
    return candidateRepository.save(new Candidate(email));
  }

  public User save(User coordinator) {
    return coordinatorRepository.save(coordinator);
  }

  public User save(Interviewer interviewer) {
    return interviewerRepository.save(interviewer);
  }

  public User save(Candidate candidate) {
    return candidateRepository.save(candidate);
  }

  /**
   * Returns a user of given id or generates an ApplicationErrorException if none found.
   *
   * @param id user id
   * @return user with any role
   */
  public User getCoordinatorById(Long id) {
    try {
      return (User) Hibernate.unproxy(coordinatorRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw new CoordinatorNotFoundException(id);
    }
  }

  /**
   * Removes coordinator from database if id is valid or throws CoordinatorNotFoundException.
   *
   * @param id id to delete by
   */
  public void removeCoordinatorById(Long id) {
    try {
      coordinatorRepository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw new CoordinatorNotFoundException(id);
    }
  }

}
