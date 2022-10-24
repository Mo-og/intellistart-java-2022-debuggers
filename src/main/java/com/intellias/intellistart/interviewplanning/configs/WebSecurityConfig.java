package com.intellias.intellistart.interviewplanning.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security configuration.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  /**
   * Requests filter to perform authorisation.
   *
   * @param http HttpSecurity injected object
   * @return http filter
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .headers().disable()
        .authorizeHttpRequests(requests -> requests
            .anyRequest().permitAll() //for now allow everyone
        )
        .logout(LogoutConfigurer::permitAll);

    return http.build();
  }

  /**
   * Temporary service provider to allow in-memory authentication.
   *
   * @return in-memory user manager
   */
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user =
        User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(user);
  }
}