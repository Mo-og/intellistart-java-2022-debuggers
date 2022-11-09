package com.intellias.intellistart.interviewplanning.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("authorities", userDetails.getAuthorities());
    return doGenerateToken(claims, userDetails.getUsername());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_SECONDS * 1000))
        .signWith(getKey())
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private Key getKey() {
    if (key == null) {
      if (secret == null) {
        log.debug("Secret is {}", (Object) null);
        throw new NullPointerException("Secret for jwt token was not retrieved");
      } else {
        log.debug("Secret is {}", secret.isBlank() ? "not set" : "set successfully");
      }
      key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    return key;
  }
}