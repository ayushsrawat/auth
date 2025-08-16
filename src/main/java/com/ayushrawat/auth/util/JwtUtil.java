package com.ayushrawat.auth.util;

import com.ayushrawat.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

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
    return extractUsername(token).equals(user.getUsername()) && !isTokenExpired(token);
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

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractClaims(token);
    return claimsResolver.apply(claims);
  }

  @SuppressWarnings("unused")
  public Integer extractUserRole(String token) {
    return extractClaims(token).get("role", Integer.class);
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
