package com.intellias.intellistart.interviewplanning.repositories;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Save users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT user "
      + "FROM User user "
      + "WHERE user.email = ?1")
  User getUserByEmail(String email);

  @Transactional
  @Modifying()
  @Query("UPDATE User "
      + "SET userRole = ?2 "
      + "WHERE email = ?1")
  Integer updateRoleByEmail(String email, UserRole role);

  @Query("SELECT user "
      + "FROM User user "
      + "WHERE user.userRole = ?1")
  Set<User> getUsersWithRole(UserRole role);

  @Query("SELECT user "
      + "FROM User user "
      + "WHERE user.id = ?1 AND user.userRole = ?2")
  User getUserByIdWithRole(Long id, UserRole role);
}
