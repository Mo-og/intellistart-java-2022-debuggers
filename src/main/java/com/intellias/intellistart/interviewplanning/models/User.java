package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User implements UserDetails {

  @Id
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQUENCE", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @Column(nullable = false)
  private Long id;
  private String email;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @JsonIgnore
  @Transient
  private Collection<GrantedAuthority> authorities;

  /**
   * User.
   *
   * @param email mail
   * @param role  role
   */
  public User(String email, UserRole role) {
    this.email = email;
    this.role = role;
    authorities = List.of(role);
  }

  /**
   * UserDetails method implementation.
   *
   * @return Unmodifiable list of a single role element
   */
  public Collection<GrantedAuthority> getAuthorities() {
    if (authorities == null) {
      authorities = List.of(role);
    }
    return authorities;
  }

  @Override
  @JsonIgnore
  public String getPassword() {
    return "";
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return email;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
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
  public enum UserRole implements GrantedAuthority {
    INTERVIEWER, CANDIDATE, COORDINATOR;

    @Override
    public String getAuthority() {
      return name();
    }
  }
}
