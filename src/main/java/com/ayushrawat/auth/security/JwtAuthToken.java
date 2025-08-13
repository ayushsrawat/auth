package com.ayushrawat.auth.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class JwtAuthToken extends AbstractAuthenticationToken {

  private final String token;

  public JwtAuthToken(String token) {
    super(null);
    this.token = token;
    setAuthenticated(false);
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }
}
