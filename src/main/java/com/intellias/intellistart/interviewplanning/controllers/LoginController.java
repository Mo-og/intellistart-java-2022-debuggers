package com.intellias.intellistart.interviewplanning.controllers;

import static java.text.MessageFormat.format;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationErrorException.ErrorCode;
import com.intellias.intellistart.interviewplanning.models.User;
import com.intellias.intellistart.interviewplanning.services.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class LoginController {

  private static final String FACEBOOK_GET_TOKEN_BY_CODE_URI =
      "https://graph.facebook.com/v15.0/oauth/access_token"
          + "?client_id={0}&redirect_uri={1}&client_secret={2}&code={3}";
  private static final String FACEBOOK_TOKEN_VERIFY_URI =
      "https://graph.facebook.com/v15.0/debug_token"
          + "?input_token={0}&access_token={1}";

  private static final String FACEBOOK_GET_APP_TOKEN_URI =
      "https://graph.facebook.com/v15.0/oauth/access_token"
          + "?client_id={0}&client_secret={1}&grant_type=client_credentials";
  private static final String FACEBOOK_USER_DATA_URI =
      "https://graph.facebook.com/{0}?fields=email&access_token={1}";

  private final RestTemplate restTemplate;
  private final String clientId;
  private final String clientSecret;
  private final String facebookAuthFullLink;
  private final String facebookAuthUri;
  private final FacebookAppAccessToken appAccessToken;
  private final Map<String, String> authLink;
  private final UserService userService;


  @Autowired
  public LoginController(Environment env, RestTemplate restTemplate, UserService userService) {

    this.restTemplate = restTemplate;
    clientId = env.getProperty("facebook.client-id");
    clientSecret = env.getProperty("facebook.client-secret");
    facebookAuthFullLink = env.getProperty(
        "spring.security.oauth2.client.provider.facebook.authorization-uri");
    this.facebookAuthUri = env.getProperty("facebook.redirect-uri");
    this.userService = userService;
    authLink = Collections.singletonMap("authLink", facebookAuthFullLink);

    //needs to be updated every ~60 days
    appAccessToken = restTemplate.getForObject(
        format(FACEBOOK_GET_APP_TOKEN_URI, clientId, clientSecret),
        FacebookAppAccessToken.class);

    //click link to be redirected by facebook and authenticated in app
    log.debug("\n\tclientId: {}" + "\n\tfacebookAuthLink: {}", clientId, facebookAuthFullLink);
  }

  @GetMapping("/auth-link")
  public Map<String, String> getAuthLink() {
    log.debug("AuthLink: {}", facebookAuthFullLink);
    return authLink;
  }

  @GetMapping("/info")
  public Info getInfo(OAuth2AuthenticationToken authentication) {
    return new Info()
        .setApplication("Intellistart-InterviewMaker-Debuggers")
        .setPrincipal(
            authentication == null ? null : authentication.getPrincipal().getAttributes());
  }

  @Data
  @Accessors(chain = true)
  private static class Info {

    private String application;
    private Map<String, Object> principal;
  }

  @GetMapping("/oauth2/redirect")
  public FacebookUserProfile retrieveToken(@RequestParam String code) {

    FacebookTokenResponse token = restTemplate.getForObject(
        format(FACEBOOK_GET_TOKEN_BY_CODE_URI, clientId, facebookAuthUri, clientSecret, code),
        FacebookTokenResponse.class);

    if (token == null) {
      log.info("Acquiring user token failed");
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
          "Could not get user token for provided auth code");
    } else {
      log.debug("Got user token from code: of [{}] type", token.tokenType);
    }

    FacebookTokenData tokenData = restTemplate.getForObject(
        format(FACEBOOK_TOKEN_VERIFY_URI, token.accessToken, appAccessToken.getAccessToken()),
        FacebookTokenData.class);

    log.debug("Token Data: {}", tokenData);

    if (tokenData == null || !tokenData.data.isValid()) {
      throw new ApplicationErrorException(ErrorCode.INVALID_USER_CREDENTIALS,
          "Could not verify user token");
    }

    FacebookUserProfile userProfile = restTemplate.getForObject(
        format(FACEBOOK_USER_DATA_URI, tokenData.data.userId, token.accessToken),
        FacebookUserProfile.class);

    if (userProfile == null || userProfile.getEmail() == null || userProfile.getEmail().isBlank()) {
      throw new ApplicationErrorException(ErrorCode.NO_USER_DATA,
          "Unable to get email of user from provider");
    }

    if (!userService.existsWithEmail(userProfile.getEmail())) {
      //todo jwt as candidate
    }

    User user = userService.getByEmail(userProfile.getEmail());
    //todo jwt as actual user

    return userProfile;
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
    private static class FbData {


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
      private String error;
    }

  }

  @Data
  static class FacebookUserProfile {

    String email;
  }

}

































