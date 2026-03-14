package com.ayushrawat.auth.security;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider implements AuthenticationProvider {

  private final SecureUserDetailsService secureUserDetailsService;
  private final JwtUtil jwtUtil;

  @Override
  public Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
    String token = ((JwtAuthToken) authentication).getToken();
    final String username = jwtUtil.extractUsername(token);
    if (username == null) {
      throw new BadCredentialsException("Invalid JWT token");
    }

    SecureUser secureUser = (SecureUser) secureUserDetailsService.loadUserByUsername(username);
    User user = secureUser.user();
    if (!jwtUtil.validateToken(token, user)) {
      throw new BadCredentialsException("JWT token validation failed");
    }
    return new UsernamePasswordAuthenticationToken(
      secureUser,
      null,
      secureUser.getAuthorities()
    );
  }

  @Override
  public boolean supports(@NonNull Class<?> authentication) {
    return JwtAuthToken.class.isAssignableFrom(authentication);
  }
}
