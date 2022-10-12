package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.models.Interviewer;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.InterviewerRepository;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final InterviewerRepository interviewerRepository;

  @Autowired
  public UserService(UserRepository repository, InterviewerRepository interviewerRepository) {
    this.userRepository = repository;
    this.interviewerRepository = interviewerRepository;
  }

  /**
   * Creates user and saves it to database.
   *
   * @param email user email
   * @param role  user role according to which permissions will be granted
   * @return user with generated id
   */
  public User create(String email, UserRole role) {
    if (role == UserRole.INTERVIEWER) {
      return interviewerRepository.save(new Interviewer(email));
    }
    return userRepository.save(new User(email, role));
  }

  public User create(String email) {
    User user = new User(email, UserRole.CANDIDATE);
    System.out.println(user);
    return userRepository.save(user);
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  /**
   * Returns a user of given id or generates an ApplicationErrorException if none found.
   *
   * @param id user id
   * @return user with any role
   */
  public User getById(Long id) {
    try {
      return (User) Hibernate.unproxy(userRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw new UserNotFoundException(id);
    }
  }

  public void removeUser(Long id) {
    userRepository.deleteById(id);
  }

}
