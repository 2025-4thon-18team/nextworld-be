package com.likelion.nextworld.domain.user.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

  private static final String SECRET_KEY =
      "this_is_a_very_secret_jwt_key_for_nextworld_project_123!";
  private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60;
  private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

  private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

  // Access Token 생성
  public String generateAccessToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 토큰에서 이메일 추출
  public String getEmailFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return claims.getSubject();
  }
}
