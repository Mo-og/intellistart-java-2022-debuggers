package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

  @Id
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQUENCE", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @Column(nullable = false)
  private Long id;
  private String email;
  private UserRole role;

  /**
   * User.
   *
   * @param email    mail
   * @param role role
   */
  public User(String email, UserRole role) {
    this.email = email;
    this.role = role;
  }

  public String getEmail() {
    return email;
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
    if (id != null) {
      return Objects.equals(id, user.id);
    }
    return Objects.equals(email, user.email) && role == user.role;
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
