package com.intellias.intellistart.interviewplanning.services;

import static java.text.MessageFormat.format;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtTokenUtil;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for managing authentication. Involves jwt token generation, and authentication on
 * facebook servers.
 */
@Service
@Slf4j
public class AuthService {

  private final String facebookGetTokenByCodeUri;
  private final String facebookTokenVerifyUri;
  private final String facebookUserDataUri;
  private final RestTemplate restTemplate;
  private final FacebookAppAccessToken appAccessToken;
  private final UserService userService;
  private final JwtTokenUtil jwtTokenUtil;

  /**
   * Initiates required URIs from environment variables.
   *
   * @param env object to retrieve project environment variables
   */
  public AuthService(Environment env, RestTemplate restTemplate, UserService userService,
      JwtTokenUtil jwtTokenUtil) {
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.jwtTokenUtil = jwtTokenUtil;

    facebookGetTokenByCodeUri = env.getProperty("facebook.uri.get_token_by_code");
    facebookTokenVerifyUri = env.getProperty("facebook.uri.token_verify");
    facebookUserDataUri = env.getProperty("facebook.uri.user_data");

    //needs to be updated every ~60 days
    appAccessToken = restTemplate.getForObject(
        Objects.requireNonNull(env.getProperty("facebook.uri.get_app_token")),
        FacebookAppAccessToken.class);

  }

  /**
   * Verifies facebook code and exchanges it for facebook token. Then makes needed requests to
   * Facebook servers for user data retrieval, finds user in app's database and generates jwt token
   * with their username and authorities.
   *
   * @param code facebook auth code
   * @return json of jwt token of this app
   */
  public ResponseEntity<?> generateTokenByFacebookCode(String code) {
    FacebookTokenResponse token = restTemplate.getForObject(
        format(facebookGetTokenByCodeUri, code),
        FacebookTokenResponse.class);
    if (token == null) {
      log.info("Acquiring user token by code failed");
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
          ": could not get user token for provided auth code");
    } else {
      log.debug("Got facebook token of [{}] type", token.tokenType);
    }
    return generateTokenByFacebookToken(token.accessToken);
  }

  /**
   * Makes needed requests to Facebook servers for user data retrieval, finds user in app's database
   * and generates jwt token with their username and authorities.
   *
   * @param token facebook token
   * @return json of jwt token of this app
   */
  @SneakyThrows
  public ResponseEntity<?> generateTokenByFacebookToken(String token) {
    if (token == null || token.isBlank()) {
      log.info("Provided user token is empty");
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
          ": invalid facebook token");
    } else {
      log.debug("Got facebook token from code: {}", token);
    }

    FacebookTokenData tokenData = restTemplate.getForObject(
        format(facebookTokenVerifyUri, token, appAccessToken.getAccessToken()),
        FacebookTokenData.class);

    log.debug("Token data retrieved: {}", tokenData);

    if (tokenData == null || !tokenData.data.isValid()) {
      if (tokenData != null) {
        throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
            ": could not verify user token. " + tokenData.data.error.message);
      }
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
          ": could not verify user token");
    }

    FacebookUserProfile userProfile = restTemplate.getForObject(
        format(facebookUserDataUri, tokenData.data.userId, token),
        FacebookUserProfile.class);

    if (userProfile == null || userProfile.getEmail() == null || userProfile.getEmail().isBlank()) {
      throw new ApplicationErrorException(ErrorCode.NO_USER_DATA,
          ": unable to get email of user from provider");
    }
    //todo replace with OAuth2AccessToken
    JwtToken generatedJwtToken;
    if (userService.existsWithEmail(userProfile.getEmail())) {
      User user = userService.getByEmail(userProfile.getEmail());
      generatedJwtToken = new JwtToken(jwtTokenUtil.generateToken(user));
    } else {
      generatedJwtToken = new JwtToken(
          jwtTokenUtil.generateToken(
              new User(userProfile.getEmail(), UserRole.CANDIDATE)
          )
      );
    }
    return ResponseEntity.ok(generatedJwtToken);
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


  @Data
  static class FacebookAppAccessToken {

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("token_type")
    private String tokenType;

  }

  @Data
  static class FacebookTokenResponse {

    @JsonAlias("access_token")
    String accessToken;

    @JsonAlias("token_type")
    String tokenType;
    @JsonAlias("expires_in")
    String expiresIn;
  }

  @Data
  static class FacebookTokenData {

    private FbData data;

    @Data
    public static class FbData {


      @JsonAlias("app_id")
      private String appId;

      private String type;
      private String application;
      @JsonAlias("data_access_expires_at")
      private long dataAccessExpiresAt;
      @JsonAlias("expires_at")
      private long expiresAt;
      @JsonAlias("is_valid")
      private boolean isValid;
      @JsonAlias("issued_at")
      private long issuedAt;
      private List<String> scopes;
      @JsonAlias("user_id")
      private String userId;
      private Error error;
    }

    @Data
    public static class Error {

      int code;
      String message;
      int subcode;
    }
  }

  @Data
  static class FacebookUserProfile {

    String email;
  }

}
