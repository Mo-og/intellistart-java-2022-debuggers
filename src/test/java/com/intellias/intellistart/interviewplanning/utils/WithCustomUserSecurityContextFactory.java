package com.intellias.intellistart.interviewplanning.utils;

import com.intellias.intellistart.interviewplanning.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@TestComponent
final class WithCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithCustomUser> {

  private final UserDetailsService userDetailsService;

  @Autowired
  public WithCustomUserSecurityContextFactory(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  public SecurityContext createSecurityContext(WithCustomUser withUser) {
    String username = withUser.value();
    User principal = (User) userDetailsService.loadUserByUsername(username);
    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
        principal.getAuthorities());
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }
}