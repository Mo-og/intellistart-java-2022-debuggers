package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Oauth2 user details class.
 */

@RequiredArgsConstructor
public class CustomOauth2User implements OAuth2User {

  private static final List<GrantedAuthority> CANDIDATE_AUTHORITY_LIST = List.of(
      UserRole.CANDIDATE);
  private final OAuth2User oauth2User;
  private final User user;

  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user == null ? CANDIDATE_AUTHORITY_LIST : user.getAuthorities();
  }

  @Override
  public String getName() {
    return oauth2User.getAttribute("name");
  }

  public String getEmail() {
    return oauth2User.getAttribute("email");
  }

  public String getFacebookUserId() {
    return oauth2User.getAttribute("id");
  }

  public User getUser() {
    return user;
  }

  @Override
  public String toString() {
    return MessageFormat.format("{0}:'{'email:{1}, role:{2}'}'",
        CustomOauth2User.class.getSimpleName(), getEmail(), getAuthorities());
  }
}
