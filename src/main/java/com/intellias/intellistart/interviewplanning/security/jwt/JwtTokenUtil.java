package com.intellias.intellistart.interviewplanning.security.jwt;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.intellias.intellistart.interviewplanning.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Component;

/**
 * Custom JWT tokens utility class. Includes token parsing, validating and generating
 */
@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

  public static final long JWT_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60L;
  private static final long serialVersionUID = -2550185165626007488L;
  @Value("${jwt.secret}")
  private String secret;
  private Key key;

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * Generates token string with username, issued time, expire time and authorities.
   *
   * @param userDetails user data object to get username and authorities from
   * @return token String
   */
  public OAuth2AccessToken generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", userDetails.getAuthorities());
    if (userDetails instanceof User) {
      User user = (User) userDetails;
      claims.put("fb_id", user.getFacebookId());
      claims.put("first_name", user.getFirstName());
      claims.put("middle_name", user.getMiddleName());
      claims.put("last_name", user.getLastName());
    }
    return doGenerateToken(claims, userDetails.getUsername());
  }

  private OAuth2AccessToken doGenerateToken(Map<String, Object> claims, String subject) {
    Date issuedAt = new Date(System.currentTimeMillis());
    Date expiresAt = new Date(issuedAt.getTime() + JWT_TOKEN_VALIDITY_SECONDS * 1000);
    String tokenValue = Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(issuedAt)
        .setExpiration(expiresAt)
        .signWith(getKey())
        .compact();
    //to keep api same as it was before: "tokenValue" -> "token"
    class Oauth2AccessTokenFormatted extends OAuth2AccessToken {

      public Oauth2AccessTokenFormatted(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
        super(tokenType, tokenValue, issuedAt, expiresAt);
      }

      @Override
      @JsonGetter("token")
      public String getTokenValue() {
        return super.getTokenValue();
      }
    }

    return new Oauth2AccessTokenFormatted(TokenType.BEARER, tokenValue, issuedAt.toInstant(), expiresAt.toInstant());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private Key getKey() {
    if (key != null) {
      return key;
    }
    if (secret == null || secret.isBlank()) {
      log.error("JWT secret is not set");
      throw new NullPointerException("Secret for jwt token was not retrieved");
    }
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return key;
  }
}