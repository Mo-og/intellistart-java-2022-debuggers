package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.services.AuthService;
import com.intellias.intellistart.interviewplanning.services.AuthService.JwtToken;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for authentication processes and jwt token acquiring.
 */
@RestController
@Slf4j
public class AuthController {

  private final Map<String, String> authLink;

  private final AuthService authService;

  /**
   * Initiates authorization link for fast token acquisition.
   *
   * @param env object to retrieve project environment variables
   */
  @Autowired
  public AuthController(Environment env, AuthService authService) {
    this.authService = authService;

    String facebookAuthFullLink = env.getProperty(
        "spring.security.oauth2.client.provider.facebook.authorization-uri");
    authLink = Collections.singletonMap("authLink", facebookAuthFullLink);

    //click link to be redirected by facebook and authenticated in app
    log.debug("\n\tfacebookAuthLink: {}", facebookAuthFullLink);
  }

  @GetMapping("/auth-link")
  public Map<String, String> getAuthLink() {
    return authLink;
  }

  @GetMapping({"/authenticate/redirect"})
  public ResponseEntity<?> retrieveToken(@RequestParam String code) {
    return authService.generateTokenByFacebookCode(code);
  }

  @RequestMapping(value = "/authenticate/code", method = {RequestMethod.POST, RequestMethod.GET})
  public ResponseEntity<?> retrieveTokenByCodeJson(@RequestBody JwtCode jwtCode) {
    return retrieveToken(jwtCode.code);
  }

  @RequestMapping(value = "/authenticate/token", method = {RequestMethod.POST, RequestMethod.GET})
  public ResponseEntity<?> retrieveTokenByFbTokenJson(@RequestBody JwtToken jwtToken) {
    return authService.generateTokenByFacebookToken(jwtToken.getToken());
  }

  @AllArgsConstructor
  @NoArgsConstructor
  static class JwtCode {

    private String code;
  }
}

































