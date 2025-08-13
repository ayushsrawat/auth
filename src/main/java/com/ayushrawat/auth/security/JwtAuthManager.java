package com.ayushrawat.auth.security;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthManager implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String token = ((JwtAuthToken) authentication).getToken();
    final String username = jwtUtil.extractUsername(token);
    if (username == null) {
      throw new BadCredentialsException("Invalid JWT token");
    }

    User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    if (!jwtUtil.validateToken(token, user)) {
      throw new BadCredentialsException("JWT token validation failed");
    }

    SecureUser secureUser = new SecureUser(user);
    return new UsernamePasswordAuthenticationToken(
      secureUser,
      null,
      secureUser.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthToken.class.isAssignableFrom(authentication);
  }
}
