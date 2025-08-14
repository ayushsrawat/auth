package com.ayushrawat.auth.util;

import com.ayushrawat.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret.key}")
  private String JWT_SECRET_KEY;

  @Value("${jwt.access.duration.ms}")
  private Long JWT_ACCESS_TOKEN_DURATION_MS;

  public String generateToken(User user) {
    return Jwts.builder()
      .subject(user.getUsername())
      .claim("role", user.getRole())
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_DURATION_MS))
      .signWith(getSigningKey())
      .compact();
  }

  public boolean validateToken(String token, User user) {
    return extractUsername(token).equals(user.getUsername());
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  @SuppressWarnings("unused")
  public Integer extractUserRole(String token) {
    return extractClaims(token).get("role", Integer.class);
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
