package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.configs.oauth.CustomAuthorizationRedirectFilter;
import com.intellias.intellistart.interviewplanning.configs.oauth.CustomAuthorizationRequestResolver;
import com.intellias.intellistart.interviewplanning.configs.oauth.CustomAuthorizedClientService;
import com.intellias.intellistart.interviewplanning.configs.oauth.CustomStatelessAuthorizationRequestRepo;
import com.intellias.intellistart.interviewplanning.configs.oauth.OAuthHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

  private final CustomStatelessAuthorizationRequestRepo authRepo;
  private final CustomAuthorizedClientService userService;
  private final OAuthHandler oAuthHandler;
  private final CustomAuthorizedClientService customAuthorizedClientService;
  private final CustomAuthorizationRedirectFilter customAuthorizationRedirectFilter;
  private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
  private final CustomStatelessAuthorizationRequestRepo customStatelessAuthorizationRequestRepo;

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
//
//    http.sessionManagement(
//        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeRequests(authorize -> authorize
//            .anyRequest().permitAll() //!!
            .antMatchers("/", "/login/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
    );

    http.oauth2Login(config -> {
          config.authorizationEndpoint(subconfig -> {
            subconfig.baseUri(OAuthHandler.AUTHORIZATION_BASE_URL);
            subconfig.authorizationRequestResolver(this.customAuthorizationRequestResolver);
            subconfig.authorizationRequestRepository(this.customStatelessAuthorizationRequestRepo);
          });
          config.redirectionEndpoint(subconfig -> {
            subconfig.baseUri(OAuthHandler.CALLBACK_BASE_URL + "/*");
          });
          config.authorizedClientService(this.customAuthorizedClientService);
          config.successHandler(this.oAuthHandler::oauthSuccessResponse);
          config.failureHandler(this.oAuthHandler::oauthFailureResponse);
        })
        // Filters
        .addFilterBefore(this.customAuthorizationRedirectFilter,
            OAuth2AuthorizationRequestRedirectFilter.class)
        // Auth exceptions
        .exceptionHandling(config -> {
          config.accessDeniedHandler(this::accessDenied);
          config.authenticationEntryPoint(this::accessDenied);
        });

    return http.build();
  }

  @SneakyThrows
  private void accessDenied(HttpServletRequest request, HttpServletResponse response,
      Exception authException) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{ \"error\": \"Access Denied\" }");
  }

}