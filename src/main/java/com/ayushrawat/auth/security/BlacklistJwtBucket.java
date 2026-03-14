package com.ayushrawat.auth.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * JWT blacklisting with Caffeine
 *
 * @see <a href="https://medium.com/@AlexanderObregon/jwt-blacklisting-in-spring-boot-for-revoked-sessions-9041592585be">jwt-blacklisting-in-spring-boot-for-revoked-sessions</a>
 */
@Component
public class BlacklistJwtBucket {

  // Store the JTI as the key and the remaining TTL (in ms) as the value
  private final Cache<@NonNull String, @Nullable Long> blockedTokens;

  public BlacklistJwtBucket() {
    this.blockedTokens = Caffeine
        .newBuilder()
        .expireAfter(new Expiry<@NonNull String, @NonNull Long>() {
          @Override
          public long expireAfterCreate(String key, Long remainingTtlMillis, long currentTime) {
            // caffeine's expiry expects nanoseconds
            return Duration.ofMillis(remainingTtlMillis).toNanos();
          }

          @Override
          public long expireAfterUpdate(String key, Long remainingTtlMillis, long currentTime, long currentDuration) {
            return currentDuration;
          }

          @Override
          public long expireAfterRead(String key, Long remainingTtlMillis, long currentTime, long currentDuration) {
            return currentDuration;
          }
        }).build();
  }

  /**
   * @param jti                       The JWT ID
   * @param remainingDurationInMillis The exact time left until this token expires
   */
  public void add(String jti, long remainingDurationInMillis) {
    if (jti != null && remainingDurationInMillis > 0) {
      blockedTokens.put(jti, remainingDurationInMillis);
    }
  }

  public boolean contains(String jti) {
    return jti != null && blockedTokens.getIfPresent(jti) != null;
  }

}
