package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.models.User;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

  private final JwtEncoder accessTokenEncoder;

  private final JwtEncoder refreshTokenEncoder;

  @Autowired
  public TokenGenerator(JwtEncoder accessTokenEncoder,
      @Qualifier("jwtRefreshTokenEncoder") JwtEncoder refreshTokenEncoder) {
    this.accessTokenEncoder = accessTokenEncoder;
    this.refreshTokenEncoder = refreshTokenEncoder;
  }

  private String createAccessToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    Instant now = Instant.now();

    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .issuer("Intellias Interview App by Debuggers")
        .issuedAt(now)
        .expiresAt(now.plus(5, ChronoUnit.MINUTES))
        .subject(user.getEmail())
        .build();
    return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }

  private String createRefreshToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    Instant now = Instant.now();

    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .issuer("Intellias Interview App by Debuggers")
        .issuedAt(now)
        .expiresAt(now.plus(30, ChronoUnit.DAYS))
        .subject(user.getEmail())
        .build();
    return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }

  public TokenDTO createToken(Authentication authentication) {
    if (!(authentication.getPrincipal() instanceof User)) {
      throw new BadCredentialsException(
          MessageFormat.format(
              "Principal {0} is not of User type", authentication.getPrincipal()
          ));
    }
    User user = (User) authentication.getPrincipal();
    TokenDTO tokenDTO = new TokenDTO();
    tokenDTO.setUserEmail(user.getEmail());
    tokenDTO.setAccessToken(createAccessToken(authentication));

    String refreshToken;
    if (authentication.getCredentials() instanceof Jwt) {
      Jwt jwt;
      jwt = (Jwt) authentication.getCredentials();
      Instant now = Instant.now();
      Instant expiresAt = jwt.getExpiresAt();
      Duration duration = Duration.between(now, expiresAt);
      long daysUntilExpiration = duration.toDays();
      if (daysUntilExpiration < 7) {
        refreshToken = createRefreshToken(authentication);
      } else {
        refreshToken = jwt.getTokenValue();
      }
    } else {
      refreshToken = createRefreshToken(authentication);
    }
    tokenDTO.setRefreshToken(refreshToken);
    return tokenDTO;
  }
}
