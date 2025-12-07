package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.entity.RefreshToken;
import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.payload.request.LoginRequest;
import com.ayushrawat.auth.payload.request.TokenRefreshRequest;
import com.ayushrawat.auth.payload.response.LoginResponse;
import com.ayushrawat.auth.payload.response.TokenRefreshResponse;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.security.SecureUser;
import com.ayushrawat.auth.service.AuthService;
import com.ayushrawat.auth.service.RefreshTokenService;
import com.ayushrawat.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public LoginResponse login(LoginRequest request) {
    logger.info("Logging request from user [{}]", request.getUsername());
    User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
    if (passwordEncoder.matches(request.getPassword(), user.passwordHash())) {
      logger.info("creating jwt token for user : {}", request.getUsername());
      String accessToken = jwtUtil.generateToken(user);
      String refreshToken = refreshTokenService.createRefreshToken(user).token();
      return new LoginResponse(user.username(), accessToken, refreshToken);
    }
    throw new BadCredentialsException("Invalid username or password");
  }

  @Override
  public TokenRefreshResponse refreshToken(TokenRefreshRequest tokenRefreshRequest) {
    logger.info("Refresh token request for token [{}]", tokenRefreshRequest.getRefreshToken());
    return refreshTokenService.findByToken(tokenRefreshRequest.getRefreshToken()).map(refreshTokenService::verifyExpiration).map(RefreshToken::userId).map(userId -> {
      Optional<User> user = userRepository.findById(userId);
      if (user.isEmpty()) {
        throw new RuntimeException("Invalid refresh token. Please login again.");
      }
      String newAccessToken = jwtUtil.generateToken(user.get());
      return new TokenRefreshResponse(user.get().username(), newAccessToken, tokenRefreshRequest.getRefreshToken());
    }).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
  }

  @Override
  public void logoutUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getPrincipal() == null) {
      logger.error("Unable to logout user.");
      return;
    }
    var principal = (SecureUser) authentication.getPrincipal();
    User user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
    int delta = refreshTokenService.deleteByUser(user);
    logger.info("logging [{}] out, deleting refresh tokens [{}] size", user.username(), delta);
  }

}