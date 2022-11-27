package com.intellias.intellistart.interviewplanning.services;

import static java.text.MessageFormat.format;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.exceptions.TemplateMessageException;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.security.jwt.JwtTokenUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
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

  public final boolean isOffline;
  public final String offlineUserEmail;
  public final String facebookGetTokenByCodeUri;
  public final String facebookTokenVerifyUri;
  public final String facebookUserProfileUri;
  public final RestTemplate rest;
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
    if (token == null || token.hasErrors()) {
      log.info("Acquiring user token by code failed");
      throw new TemplateMessageException(ErrorCode.INVALID_USER_CREDENTIALS,
          ": could not get user token for provided auth code. " + ((token == null || token.getError() == null) ? ""
              : format("{0}, error type: {1}, error code: {2}, fb trace id: {3}", token.getError().getMessage(),
                  token.getError().getType(), token.getError().getCode(), token.getError().getFbTraceId())));
    } else {
      log.debug("Got facebook token of [{}] type", token.getTokenType());
    }
    return generateJwtByFacebookToken(token.getAccessToken());
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
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS, "Invalid facebook token");
    } else {
      log.debug("Got facebook token from code: {}", token);
    }

    FacebookUserProfile fbProfile = getFacebookUserProfile(token);

    if (fbProfile == null || fbProfile.getEmail() == null || fbProfile.getEmail().isBlank()) {
      throw new ApplicationErrorException(ErrorCode.NO_USER_DATA, "Unable to get email of user from provider");
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
    FacebookTokenInfo tokenData = rest.getForObject(
        format(facebookTokenVerifyUri, token, appAccessToken.getAccessToken()), FacebookTokenInfo.class);

    log.debug("Token data retrieved: {}", tokenData);

    if (tokenData == null || !tokenData.data.isValid()) {
      if (tokenData != null) {
        throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
            "Cannot verify user token. " + tokenData.data.error.message);
      }
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS, "Cannot verify user token");
    }

    return rest.getForObject(format(facebookUserProfileUri, tokenData.data.userId, token), FacebookUserProfile.class);
  }

  /**
   * FacebookAppAccessToken DTO class for getting app access token.
   */
  @AllArgsConstructor
  public static class FacebookAppAccessToken {

    @Getter
    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("token_type")
    private String tokenType;

  }

  /**
   * Facebook token response DTO.
   */
  @Data
  @Accessors(chain = true)
  public static class FacebookTokenResponse {

    @JsonAlias("access_token")
    private String accessToken;

    @JsonAlias("token_type")
    private String tokenType;
    private Error error;

    public boolean hasErrors() {
      return error != null;
    }

    /**
     * Facebook token response errors DTO.
     */
    @Data
    public static class Error {

      private int code;
      private String message;
      private String type;
      @JsonAlias("fbtrace_id")
      private String fbTraceId;
    }
  }

  /**
   * Facebook token data wrapper DTO.
   */
  @Data
  @Accessors(chain = true)
  public static class FacebookTokenInfo {

    private FbData data;

    /**
     * Facebook token data DTO.
     */
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

    /**
     * Facebook token data errors DTO.
     */
    @Data
    public static class Error {

      private int code;
      private String message;
      private int subcode;
    }
  }

  /**
   * FacebookUserProfile DTO. Retrieved from Facebook
   */
  @Data
  @Accessors(chain = true)
  public static class FacebookUserProfile {

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
