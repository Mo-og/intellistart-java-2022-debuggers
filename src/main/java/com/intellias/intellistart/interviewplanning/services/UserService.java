package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
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

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Creates user and saves it to database.
   *
   * @param email user email
   * @param role  user role according to which permissions will be granted
   * @return user with generated id
   */
  public User create(String email, UserRole role) {
    return userRepository.save(new User(email, role));
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
  public User getUserById(Long id) {
    try {
      return (User) Hibernate.unproxy(userRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw NotFoundException.coordinatorNotFound(id);
    }
  }

  /**
   * Removes coordinator from database if id is valid or throws CoordinatorNotFoundException.
   *
   * @param id id to delete by
   */
  public void removeUserById(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw NotFoundException.coordinatorNotFound(id);
    }
  }

}
