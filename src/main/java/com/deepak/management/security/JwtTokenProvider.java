package com.deepak.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value(
      "${jwt.secret:mfQ3b0HVp6zrzyqP+1m+ZpF7Itd03p2r5ZOU5tPjVgmCL8nspBWyWqN4t4nx3DBQhct+GPtTznhMRl+GcUMWQg==}")
  private String jwtSecret;

  @Value("${jwt.expiration-in-ms:3600000}")
  private long jwtExpirationInMs;

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String username, String role, List<Integer> clinicIds) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .claim("role", role)
        .claim("clinicIds", clinicIds) // Add clinicIds to the token
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  public String getUsernameFromJWT(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
      return true;
    } catch (Exception ex) {
      // Log the exception if necessary
    }
    return false;
  }

  public String getRoleFromJWT(String token) {
    return getClaimsFromToken(token).get("role", String.class);
  }

  @SuppressWarnings("unchecked")
  public List<Integer> getClinicIdsFromJWT(String token) {
    return getClaimsFromToken(token).get("clinicIds", List.class);
  }

  private Claims getClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
