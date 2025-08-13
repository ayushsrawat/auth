package com.ayushrawat.auth.service;

import com.ayushrawat.auth.entity.RefreshToken;
import com.ayushrawat.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {

  RefreshToken createRefreshToken(User user);

  Optional<RefreshToken> findByToken(String refreshToken);

  RefreshToken verifyExpiration(RefreshToken refreshToken);

  int deleteByUser(User user);

}
