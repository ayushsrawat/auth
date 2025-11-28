package com.ayushrawat.auth.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class JwtAuthToken extends AbstractAuthenticationToken {

  private final String token;

  public JwtAuthToken(String token) {
    super(AuthorityUtils.NO_AUTHORITIES);
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
