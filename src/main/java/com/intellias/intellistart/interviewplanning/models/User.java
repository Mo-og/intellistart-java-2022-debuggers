package com.intellias.intellistart.interviewplanning.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * User.
 */
@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
@RequiredArgsConstructor
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class User implements UserDetails, OAuth2User {

  @Id
  @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @Column(nullable = false)
  private Long id;
  private String email;
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @JsonAlias("facebook_id")
  private Long facebookId;
  @JsonAlias("first_name")
  private String firstName;
  @JsonAlias("middle_name")
  private String middleName;
  @JsonAlias("last_name")
  private String lastName;
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

  @Override
  public Map<String, Object> getAttributes() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    map.put("email", email);
    map.put("role", role);
    map.put("facebookId", facebookId);
    map.put("firstName", firstName);
    map.put("middleName", middleName);
    map.put("lastName", lastName);
    return map;
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

  @Override
  public String getName() {
    return getUsername();
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
