package com.intellias.intellistart.interviewplanning.models;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

/**
 * User.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "COORDINATOR")
public class User {

  @Id
  @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @Column(nullable = false)
  private Long id;
  private String email;
  private UserRole userRole;

  /**
   * User.
   *
   * @param email    mail
   * @param userRole role
   */
  public User(String email, UserRole userRole) {
    this.email = email;
    this.userRole = userRole;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    User user = (User) o;
    return id != null && Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  /**
   * User role.
   */
  public enum UserRole {
    INTERVIEWER, CANDIDATE, COORDINATOR
  }
}
