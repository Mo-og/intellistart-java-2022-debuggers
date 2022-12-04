package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseBad;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.test_utils.TestUtils.json;
import static java.text.MessageFormat.format;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.intellias.intellistart.interviewplanning.controllers.AuthController.JwtCode;
import com.intellias.intellistart.interviewplanning.controllers.AuthController.JwtToken;
import com.intellias.intellistart.interviewplanning.models.User.UserRole;
import com.intellias.intellistart.interviewplanning.services.AuthService;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo.Error;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo.FbData;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenResponse;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookUserProfile;
import com.intellias.intellistart.interviewplanning.test_utils.DisabledWhenOffline;
import com.intellias.intellistart.interviewplanning.test_utils.TestSecurityUtils;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc
@Slf4j
class AuthControllerTest {

  public static final String FB_TOKEN_VALUE = "FB_TOKEN";
  private static final FacebookTokenResponse fbToken = new FacebookTokenResponse().setAccessToken(FB_TOKEN_VALUE);
  private static final FacebookUserProfile fbUserProfile = new FacebookUserProfile().setEmail(
      TestSecurityUtils.COORDINATOR_EMAIL);
  private static final FacebookUserProfile fbUserProfileNotInDb = new FacebookUserProfile().setEmail(
      "someNewEmail@mail.com");
  private static final FacebookTokenInfo fbTokenInfo = new FacebookTokenInfo().setData(
      new FbData().setValid(true).setUserId("1"));
  private static final FacebookTokenInfo fbTokenInfoInvalid = new FacebookTokenInfo().setData(
      new FbData().setValid(false).setUserId("1")
          .setError(new Error().setCode(403).setSubcode(1).setMessage("Wrong credentials test")));
  @Autowired
  Environment env;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AuthService authService;
  @SpyBean
  private RestTemplate rest;


  @Test
  void getAuthLink() {
    Map<String, String> authLink = Collections.singletonMap("authLink",
        env.getProperty("spring.security.oauth2.client.provider.facebook.authorization-uri"));
    checkResponseOk(get("/auth-link"), null, json(authLink), mockMvc);
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByCode() {
    //get FB token
    doReturn(fbToken).when(rest).getForObject(any(String.class), eq(FacebookTokenResponse.class));

    //get token info with FB user id
    doReturn(fbTokenInfo).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));

    String fbUserUri = authService.facebookUserProfileUri;
    assert fbUserUri != null;
    doReturn(fbUserProfile).when(rest)
        .getForObject(format(fbUserUri, fbTokenInfo.getData().getUserId(), FB_TOKEN_VALUE), FacebookUserProfile.class);

    String token = checkResponseOk(get("/authenticate/code"), json(new JwtCode("code")), null, mockMvc);
    token = extractTokenValue(token);
    String responseJson = checkResponseOk(get("/me").header("Authorization", "Bearer " + token), null, null, mockMvc);
    assertTrue(responseJson.contains("\"role\":\"" + UserRole.COORDINATOR.name() + "\""));
  }

  private String extractTokenValue(String json) {
    log.debug("TokenData: {}", json);
    json = json.substring(json.indexOf("\"token\":\"") + 9);
    json = json.substring(0, json.indexOf('"'));
    log.debug("TokenSubstring: {}", json);
    return json;
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByToken() {
    //get token info with FB user id
    doReturn(fbTokenInfo).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));

    String fbUserUri = authService.facebookUserProfileUri;
    assert fbUserUri != null;
    doReturn(fbUserProfile).when(rest)
        .getForObject(format(fbUserUri, fbTokenInfo.getData().getUserId(), FB_TOKEN_VALUE), FacebookUserProfile.class);

    String token = checkResponseOk(get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)), null, mockMvc);
    token = extractTokenValue(token);
    String responseJson = checkResponseOk(get("/me").header("Authorization", "Bearer " + token), null, null, mockMvc);
    assertTrue(responseJson.contains("\"role\":\"" + UserRole.COORDINATOR.name() + "\""));
  }


  @Test
    //integration
  void getAuthByTokenUserNotFoundGivesCandidateRole() {
    //get token info with FB user id
    doReturn(fbTokenInfo).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));

    String fbUserUri = authService.facebookUserProfileUri;
    assert fbUserUri != null;
    doReturn(fbUserProfileNotInDb).when(rest)
        .getForObject(format(fbUserUri, fbTokenInfo.getData().getUserId(), FB_TOKEN_VALUE), FacebookUserProfile.class);

    String token = checkResponseOk(get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)), null, mockMvc);
    token = extractTokenValue(token);
    String responseJson = checkResponseOk(get("/me").header("Authorization", "Bearer " + token), null, null, mockMvc);
    assertTrue(responseJson.contains("\"role\":\"" + UserRole.CANDIDATE.name() + "\""));
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByTokenWrongCredentials() {
    //get token info with FB user id
    doReturn(fbTokenInfoInvalid).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));

    String fbUserUri = authService.facebookUserProfileUri;
    assert fbUserUri != null;
    doReturn(fbUserProfileNotInDb).when(rest).getForObject(
        format(fbUserUri, fbTokenInfoInvalid.getData().getUserId(), FB_TOKEN_VALUE), FacebookUserProfile.class);

    checkResponseBad(get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)), null, status().isBadRequest(),
        mockMvc);
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByTokenNoEmailInFbProfile() {
    //get token info with FB user id
    doReturn(fbTokenInfo).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));

    String fbUserUri = authService.facebookUserProfileUri;
    assert fbUserUri != null;
    doReturn(new FacebookUserProfile())
        .when(rest)
        .getForObject(format(fbUserUri, fbTokenInfoInvalid.getData().getUserId(), FB_TOKEN_VALUE),
            FacebookUserProfile.class);//no email set

    checkResponseBad(
        get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)),
        null, status().isBadRequest(), mockMvc
    );
  }

  @Test
    //integration
  void getAuthByCodeInvalidCode() {
    var tokenResponse = new FacebookTokenResponse().setError(
        new FacebookTokenResponse.Error().setMessage("Test error"));
    //get token info with FB user id
    doReturn(tokenResponse).when(rest).getForObject(any(String.class), eq(FacebookTokenResponse.class));
    checkResponseBad(get("/authenticate/code"), json(new JwtCode("some code")), null, status().isBadRequest(), mockMvc);
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByTokenNullToken() {
    //get token info with FB user id
    checkResponseBad(get("/authenticate/token"), json(new JwtToken()), null, status().isBadRequest(), mockMvc);
  }

  @Test
  @DisabledWhenOffline
    //integration
  void getAuthByTokenNullTokenInfo() {
    //get token info with FB user id
    doReturn(null).when(rest).getForObject(any(String.class), eq(FacebookTokenInfo.class));
    checkResponseBad(get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)), null, status().isBadRequest(),
        mockMvc);
  }

}
