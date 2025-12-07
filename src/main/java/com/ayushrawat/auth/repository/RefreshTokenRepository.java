package com.ayushrawat.auth.repository;

import com.ayushrawat.auth.entity.RefreshToken;
import com.ayushrawat.auth.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<@NonNull RefreshToken, @NonNull Integer> {

  Optional<RefreshToken> findByToken(String token);

  @Transactional
  @Modifying
  int deleteByUser(User user);

}
