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
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret.key}")
  private String jwtSecretKey;

  @Value("${jwt.access.duration.ms}")
  private Long jwtAccessTokenDurationMs;

  public String generateToken(User user) {
    String jti = UUID.randomUUID().toString();
    return Jwts.builder()
      .subject(user.username())
      .id(jti)
      .claim("role", user.role())
      .issuedAt(new Date())
      .expiration(new Date(System.currentTimeMillis() + jwtAccessTokenDurationMs))
      .signWith(getSigningKey())
      .compact();
  }

  public boolean validateToken(String token, User user) {
    return extractUsername(token).equals(user.username()) && !isTokenExpired(token);
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public String extractUsername(String token) {
    return extractUsername(extractClaims(token));
  }

  public String extractUsername(Claims claims) {
    return claims.getSubject();
  }

  public String extractJTI(String token) {
    return extractJTI(extractClaims(token));
  }

  public String extractJTI(Claims claims) {
    return claims.getId();
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
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
