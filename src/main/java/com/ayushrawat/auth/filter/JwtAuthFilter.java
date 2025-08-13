package com.ayushrawat.auth.filter;

import com.ayushrawat.auth.security.JwtAuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final AuthenticationManager authenticationManager;

  @Override
  protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      final String token = bearerToken.substring(7);
      try {
        JwtAuthToken jwtAuthToken = new JwtAuthToken(token);
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authenticationManager.authenticate(jwtAuthToken);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (AuthenticationException e) {
        logger.warn("JWT authentication failed: {}", e.getMessage());
        throw new BadCredentialsException("JWT token authentication failed");
      }
    }
    filterChain.doFilter(request, response);
  }

}
