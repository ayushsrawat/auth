package com.ayushrawat.auth.filter;

import com.ayushrawat.auth.security.JwtAuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final AuthenticationManager authenticationManager;
  private final HandlerExceptionResolver exceptionResolver;

  public JwtAuthFilter(AuthenticationManager authenticationManager, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
    this.authenticationManager = authenticationManager;
    this.exceptionResolver = exceptionResolver;
  }

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
        exceptionResolver.resolveException(request, response, null, e);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

}
