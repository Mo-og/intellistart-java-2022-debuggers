package com.intellias.intellistart.interviewplanning.services;


import com.intellias.intellistart.interviewplanning.exceptions.NotFoundException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.repositories.UserRepository;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

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
  public User getById(Long id) {
    try {
      return (User) Hibernate.unproxy(userRepository.getReferenceById(id));
    } catch (EntityNotFoundException e) {
      throw NotFoundException.user(id);
    }
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  /**
   * Removes coordinator from database if id is valid or throws CoordinatorNotFoundException.
   *
   * @param id id to delete by
   */
  public void removeById(Long id) {
    try {
      userRepository.deleteById(id);
    } catch (EntityNotFoundException e) {
      throw NotFoundException.coordinator(id);
    }
  }

  public boolean existsWithEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public User getByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> NotFoundException.user(email));
  }

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("No user found with username " + username));
  }

}
