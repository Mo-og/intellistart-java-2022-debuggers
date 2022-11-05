package com.intellias.intellistart.interviewplanning.configs;

import com.intellias.intellistart.interviewplanning.models.User;
import java.util.Collections;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {


  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    User user = new User();
    System.out.println("*".repeat(100));
    System.out.println(jwt.toString());
    System.out.println("Claims: " + jwt.getClaims());
    System.out.println("Headers: " + jwt.getHeaders());
    System.out.println("Subject: " + jwt.getSubject());
    System.out.println("Id: " + jwt.getId());
    System.out.println("*".repeat(100));
    return new UsernamePasswordAuthenticationToken(user, jwt, Collections.emptyList());
  }
}
