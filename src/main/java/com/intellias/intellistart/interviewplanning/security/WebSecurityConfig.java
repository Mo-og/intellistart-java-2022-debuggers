package com.intellias.intellistart.interviewplanning.security;

import static com.intellias.intellistart.interviewplanning.models.User.UserRole.COORDINATOR;
import static com.intellias.intellistart.interviewplanning.models.User.UserRole.INTERVIEWER;

import com.intellias.intellistart.interviewplanning.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

/**
 * Spring security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

  private final JwtRequestFilter jwtRequestFilter;

  @Bean
  public static PropertySourcesPlaceholderConfigurer ppc() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * Requests filter to perform authorization.
   *
   * @param http HttpSecurity injected object
   * @return http filter
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.cors().disable();
    http.httpBasic().disable();

    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.addFilterBefore(this.jwtRequestFilter,
        OAuth2AuthorizationRequestRedirectFilter.class);

    http.authorizeRequests(authorize -> authorize
        .antMatchers("/authenticate/**", "/auth-link", "/").permitAll()
        .antMatchers("/bookings/**").hasAnyAuthority(COORDINATOR.name())
        .antMatchers("/interviewers/**").hasAnyAuthority(COORDINATOR.name(), INTERVIEWER.name())
        .antMatchers("/users/**").hasAuthority(COORDINATOR.name())
        .antMatchers("/weeks/{weekId}/dashboard").hasAuthority(COORDINATOR.name())
        .antMatchers("/weeks/*").permitAll()
        .anyRequest().authenticated()
    );

    return http.build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}