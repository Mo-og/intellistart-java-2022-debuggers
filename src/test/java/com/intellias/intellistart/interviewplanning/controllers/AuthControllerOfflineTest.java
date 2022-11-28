package com.intellias.intellistart.interviewplanning.controllers;

import static com.intellias.intellistart.interviewplanning.utils.TestUtils.checkResponseOk;
import static com.intellias.intellistart.interviewplanning.utils.TestUtils.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.intellias.intellistart.interviewplanning.controllers.AuthController.JwtToken;
import com.intellias.intellistart.interviewplanning.services.AuthService;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo.Error;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenInfo.FbData;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookTokenResponse;
import com.intellias.intellistart.interviewplanning.services.AuthService.FacebookUserProfile;
import com.intellias.intellistart.interviewplanning.utils.TestSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = TestSecurityUtils.class)
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles({"dev", "offline"})
class AuthControllerOfflineTest {

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
    //integration
  void getAuthByTokenOfflineCoverage() {
    String token = checkResponseOk(get("/authenticate/token"), json(new JwtToken(FB_TOKEN_VALUE)), null, mockMvc);
    String responseJson = checkResponseOk(get("/me").header("Authorization", "Bearer " + extractTokenValue(token)),
        null, null,
        mockMvc);
    System.out.println(responseJson);
  }

  private String extractTokenValue(String json) {
    log.debug("TokenData: {}", json);
    json = json.substring(json.indexOf("\"token\":\"") + 9);
    json = json.substring(0, json.indexOf('"'));
    log.debug("TokenSubstring: {}", json);
    return json;
  }

}
