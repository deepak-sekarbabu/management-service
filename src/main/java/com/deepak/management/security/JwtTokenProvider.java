package com.deepak.management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.access-token.expiration}") // 1 hour by default
  private long jwtExpirationInMs;

  @Value("${jwt.refresh-token.expiration}") // 24 hours by default
  private long refreshTokenExpirationInMs;

  @Value("${jwt.cookie.domain:}")
  private String cookieDomain;

  @Value("${jwt.cookie.secure:false}")
  private boolean cookieSecure;

  private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(String username, String role, List<Integer> clinicIds) {
    return buildToken(username, role, clinicIds, jwtExpirationInMs);
  }

  public String generateRefreshToken(String username) {
    return buildToken(username, null, null, refreshTokenExpirationInMs);
  }

  private String buildToken(
      String username, String role, List<Integer> clinicIds, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    JwtBuilder builder =
        Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey());

    if (role != null) {
      builder.claim("role", role);
    }

    if (clinicIds != null) {
      builder.claim("clinicIds", clinicIds);
    }

    return builder.compact();
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (SignatureException ex) {
      logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty");
    }
    return false;
  }

  public boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String getRoleFromJWT(String token) {
    return getClaimFromToken(token, claims -> claims.get("role", String.class));
  }

  @SuppressWarnings("unchecked")
  public List<Integer> getClinicIdsFromJWT(String token) {
    return getClaimFromToken(token, claims -> claims.get("clinicIds", List.class));
  }

  public String generateTokenFromUsername(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationInMs))
        .signWith(getSigningKey())
        .compact();
  }

  public ResponseCookie generateAccessTokenCookie(String accessToken) {
    return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
        .domain(cookieDomain.isEmpty() ? null : cookieDomain)
        .path("/")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite("Lax")
        .maxAge(jwtExpirationInMs / 1000)
        .build();
  }

  public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .domain(cookieDomain.isEmpty() ? null : cookieDomain)
        .path("/")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite("Lax")
        .maxAge(refreshTokenExpirationInMs / 1000)
        .build();
  }

  public ResponseCookie generateClearAccessTokenCookie() {
    return ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
        .domain(cookieDomain.isEmpty() ? null : cookieDomain)
        .path("/")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite("Lax")
        .maxAge(0)
        .build();
  }

  public ResponseCookie generateClearRefreshTokenCookie() {
    return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
        .domain(cookieDomain.isEmpty() ? null : cookieDomain)
        .path("/")
        .httpOnly(true)
        .secure(cookieSecure)
        .sameSite("Lax")
        .maxAge(0)
        .build();
  }

  public String extractAccessToken(HttpServletRequest request) {
    String headerToken = extractTokenFromHeader(request);
    if (headerToken != null) {
      return headerToken;
    }
    return extractAccessTokenFromCookies(request);
  }

  private String extractTokenFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public String extractAccessTokenFromCookies(HttpServletRequest request) {
    return extractCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
  }

  public String extractRefreshTokenFromCookies(HttpServletRequest request) {
    return extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
  }

  private String extractCookieValue(HttpServletRequest request, String cookieName) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
