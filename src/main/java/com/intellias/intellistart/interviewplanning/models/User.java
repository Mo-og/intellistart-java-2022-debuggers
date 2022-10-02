package com.intellias.intellistart.interviewplanning.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

/**
 * User.
 *
 */
@Entity
@Data
public class User {

  @Id
  private String id;
  private String email;
  private UserRole role;

  /**
   * User.
   *
   * @param email mail
   * @param role role
   */
  public User(String email, UserRole role) {
    this.email = email;
    this.role = role;
  }

  public User() {
  }
}
