package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.entity.RefreshToken;
import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.repository.RefreshTokenRepository;
import com.ayushrawat.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  @Value("${jwt.refresh.duration.sec}")
  private Long refreshTokenDurationSec;

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public RefreshToken createRefreshToken(User user) {
    refreshTokenRepository.deleteByUser(user);

    final RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationSec));
    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public Optional<RefreshToken> findByToken(String refreshToken) {
    return refreshTokenRepository.findByToken(refreshToken);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken refreshToken) {
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      throw new RuntimeException("Refresh token expired. Please login again.");
    }
    return refreshToken;
  }

  @Override
  public int deleteByUser(User user) {
    return refreshTokenRepository.deleteByUser(user);
  }

}
