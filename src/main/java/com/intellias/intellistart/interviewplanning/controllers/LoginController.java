package com.intellias.intellistart.interviewplanning.controllers;

import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {


  @GetMapping("/info")
  public Info getInfo(OAuth2AuthenticationToken authentication) {
    return new Info()
        .setApplication("Intellistart-InterviewMaker-Debuggers")
        .setPrincipal(authentication.getPrincipal().getAttributes());
  }

  @Data
  @Accessors(chain = true)
  private static class Info {

    private String application;
    private Map<String, Object> principal;
  }

}
