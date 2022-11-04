package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final UserService userService;

  /**
   * Requests filter to perform authorization.
   *
   * @param http HttpSecurity injected object
   * @return http filter
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      Oauth2LoginSuccessHandler successHandler) throws Exception {
    http.csrf().disable();
    http.authorizeRequests().anyRequest().authenticated();

    http.oauth2Login().userInfoEndpoint().userService(userService)
        .and().successHandler(successHandler);
    return http.build();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2Userservice() {
    return userService;
  }
}