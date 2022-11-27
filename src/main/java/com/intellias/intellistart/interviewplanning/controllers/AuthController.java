package com.intellias.intellistart.interviewplanning.controllers;

import com.intellias.intellistart.interviewplanning.services.AuthService;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
  public ResponseEntity<OAuth2AccessToken> retrieveToken(@RequestParam String code) {
    return ResponseEntity.ok(authService.generateJwtByFacebookCode(code));
  }

  @RequestMapping(value = "/authenticate/code", method = {RequestMethod.POST, RequestMethod.GET})
  public ResponseEntity<OAuth2AccessToken> retrieveTokenByCodeJson(@RequestBody JwtCode jwtCode) {
    return retrieveToken(jwtCode.code);
  }

  @RequestMapping(value = "/authenticate/token", method = {RequestMethod.POST, RequestMethod.GET})
  public ResponseEntity<OAuth2AccessToken> retrieveTokenByFbTokenJson(@RequestBody JwtToken jwtToken) {
    return ResponseEntity.ok(authService.generateJwtByFacebookToken(jwtToken.getToken()));
  }

  /**
   * Simple facebook code DTO.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JwtCode {

    private String code;
  }

  /**
   * Simple jwt token DTO.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class JwtToken {

    private String token;
  }
}

































