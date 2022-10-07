package com.intellias.intellistart.interviewplanning.services;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {

  UserRepository userRepository;

  @Autowired
  public UserService(UserRepository repository) {
    this.userRepository = repository;
  }

  public User createUser(String email, UserRole role) {
    return userRepository.save(new User(email, role));
  }

  public User createUser(String email) {
    return userRepository.save(new User(email, UserRole.CANDIDATE));
  }

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public User getUser(Long id) {
    return userRepository.getReferenceById(id);
  }

  public void removeUser(Long id) {
    userRepository.deleteById(id);
  }

}
