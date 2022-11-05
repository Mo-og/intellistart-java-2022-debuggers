package com.intellias.intellistart.interviewplanning.configs;

import lombok.Data;

@Data
public class TokenDTO {

  private String userEmail;
  private String accessToken;
  private String refreshToken;
}
