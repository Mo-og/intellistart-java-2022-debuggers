package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.services.UserService;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Custom authentication success handler for OAuth2.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  private final UserService userService;

  /**
   * Custom authentication success handler. Currently, provides logging
   */
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {
    CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
    //TODO remove debug message
    System.out.println("#".repeat(100));
    System.out.println("NAME: " + oauth2User.getName());
    System.out.println("EMAIL: " + oauth2User.getEmail());
    System.out.println("#".repeat(100));
    //
    if (userService.existsWithEmail(oauth2User.getEmail())) {
      Iterator<? extends GrantedAuthority> iterator = oauth2User.getAuthorities().iterator();
      log.info(iterator.hasNext() ? iterator.next().getAuthority() : "?" + " logged in: "
          + oauth2User.getEmail());
    } else {
      log.info("Candidate logged in: " + oauth2User.getEmail());
    }
    super.onAuthenticationSuccess(request, response, authentication);
  }
}
