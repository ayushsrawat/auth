package com.ayushrawat.auth.repository;

import com.ayushrawat.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

  RefreshToken save(RefreshToken r);

  Optional<RefreshToken> findByToken(String token);

  int deleteByUser(Integer userId);

  int delete(RefreshToken r);

}
