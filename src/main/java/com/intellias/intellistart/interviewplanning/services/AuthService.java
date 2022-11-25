package com.intellias.intellistart.interviewplanning.services;

import static java.text.MessageFormat.format;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtTokenUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for managing authentication. Involves jwt token generation, and authentication on facebook servers.
 */
@Service
@Slf4j
public class AuthService {

  private final boolean isOffline;
  private final String offlineUserEmail;
  private final String facebookGetTokenByCodeUri;
  private final String facebookTokenVerifyUri;
  private final String facebookUserProfileUri;
  private final RestTemplate rest;
  private FacebookAppAccessToken appAccessToken;
  private final UserService userService;
  private final JwtTokenUtil jwtTokenUtil;

  /**
   * Initiates required URIs from environment variables.
   *
   * @param env object to retrieve project environment variables
   */
  public AuthService(Environment env, RestTemplate rest, UserService userService, JwtTokenUtil jwtTokenUtil) {
    this.rest = rest;
    this.userService = userService;
    this.jwtTokenUtil = jwtTokenUtil;

    isOffline = Arrays.stream(env.getActiveProfiles()).anyMatch(s -> s.equalsIgnoreCase("offline"));
    if (isOffline) {
      log.error("AUTH SERVICE IS OFFLINE!");
    }

    facebookGetTokenByCodeUri = env.getProperty("facebook.uri.get_token_by_code");
    facebookTokenVerifyUri = env.getProperty("facebook.uri.token_verify");
    facebookUserProfileUri = env.getProperty("facebook.uri.user_data");
    offlineUserEmail = env.getProperty("facebook.native_user.offline.email");

    //needs to be updated every ~60 days
    appAccessToken = new FacebookAppAccessToken(env.getProperty("facebook.app-token"), "bearer");
    if (appAccessToken.accessToken == null || appAccessToken.accessToken.isBlank()) {
      log.debug("Getting new app token from Facebook");
      appAccessToken = rest.getForObject(Objects.requireNonNull(env.getProperty("facebook.uri.get_app_token"),
              "Facebook app access token property is not set. Check 'facebook.uri.get_app_token' in application.yml"),
          FacebookAppAccessToken.class);
    }

  }

  /**
   * Verifies facebook code and exchanges it for facebook token. Then makes needed requests to Facebook servers for user
   * data retrieval, finds user in app's database and generates jwt token with their username and authorities.
   *
   * @param code facebook auth code
   * @return json of jwt token of this app
   */
  public OAuth2AccessToken generateJwtByFacebookCode(String code) {
    FacebookTokenResponse token = rest.getForObject(format(facebookGetTokenByCodeUri, code),
        FacebookTokenResponse.class);
    if (token == null) {
      log.info("Acquiring user token by code failed");
      throw new ApplicationErrorException(
          ErrorCode.INVALID_USER_CREDENTIALS, ": could not get user token for provided auth code");
    } else {
      log.debug("Got facebook token of [{}] type", token.tokenType);
    }
    return generateJwtByFacebookToken(token.accessToken);
  }

  /**
   * Makes needed requests to Facebook servers for user data retrieval, finds user in app's database and generates jwt
   * token with their username and authorities.
   *
   * @param token facebook token
   * @return json of jwt token of this app
   */
  @SneakyThrows
  public OAuth2AccessToken generateJwtByFacebookToken(String token) {
    if (isOffline) {
      return jwtTokenUtil.generateToken(userService.getByEmail(offlineUserEmail));
    }

    if (token == null || token.isBlank()) {
      log.info("Provided user token is empty");
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS, ": invalid facebook token");
    } else {
      log.debug("Got facebook token from code: {}", token);
    }

    FacebookUserProfile fbProfile = getFacebookUserProfile(token);

    if (fbProfile == null || fbProfile.getEmail() == null || fbProfile.getEmail().isBlank()) {
      throw new ApplicationErrorException(ErrorCode.NO_USER_DATA, ": unable to get email of user from provider");
    }

    return makeJwtFromProfile(fbProfile);
  }

  private OAuth2AccessToken makeJwtFromProfile(FacebookUserProfile fbProfile) {
    OAuth2AccessToken generatedJwtToken;
    if (userService.existsWithEmail(fbProfile.getEmail())) {
      User user = userService.getByEmail(fbProfile.getEmail());
      if (user.getFacebookId() == null) {
        user
            .setFacebookId(fbProfile.getId())
            .setFirstName(fbProfile.getFirstName())
            .setMiddleName(fbProfile.getMiddleName())
            .setLastName(fbProfile.getLastName());
        user = userService.save(user);
      }
      generatedJwtToken = jwtTokenUtil.generateToken(user);
    } else {
      generatedJwtToken = jwtTokenUtil.generateToken(
          new User(fbProfile.getEmail(), UserRole.CANDIDATE)
              .setFacebookId(fbProfile.getId())
              .setFirstName(fbProfile.getFirstName())
              .setMiddleName(fbProfile.getMiddleName())
              .setLastName(fbProfile.getLastName()));
    }
    return generatedJwtToken;
  }

  private FacebookUserProfile getFacebookUserProfile(String token) {
    FacebookTokenData tokenData = rest.getForObject(
        format(facebookTokenVerifyUri, token, appAccessToken.getAccessToken()), FacebookTokenData.class);

    log.debug("Token data retrieved: {}", tokenData);

    if (tokenData == null || !tokenData.data.isValid()) {
      if (tokenData != null) {
        throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
            ": could not verify user token. " + tokenData.data.error.message);
      }
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS, ": could not verify user token");
    }

    return rest.getForObject(format(facebookUserProfileUri, tokenData.data.userId, token), FacebookUserProfile.class);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
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

    Long id;
    @JsonAlias("first_name")
    String firstName;
    @JsonAlias("last_name")
    String lastName;
    @JsonAlias("middle_name")
    String middleName;
    String email;
  }

}
