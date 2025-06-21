package com.deepak.management.service;

import com.deepak.management.exception.TokenRefreshException;
import com.deepak.management.model.auth.RefreshToken;
import com.deepak.management.repository.RefreshTokenRepository;
import com.deepak.management.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
  @Value("${jwt.refreshExpirationMs:86400000}") // 24 hours by default
  private long refreshTokenDurationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  /**
   * Finds a refresh token by its token string
   *
   * @param token the token string to search for
   * @return an Optional containing the refresh token if found
   */
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  /**
   * Creates a new refresh token for the specified user
   *
   * @param userId the user ID to create the token for
   * @return the created refresh token
   */
  @Transactional
  public RefreshToken createRefreshToken(Integer userId) {
    // Verify user exists
    userRepository
        .findById(userId)
        .orElseThrow(() -> new TokenRefreshException("", "User not found with id: " + userId));

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUserId(userId);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setRevoked(false);

    // Revoke existing refresh tokens for this user
    refreshTokenRepository.revokeAllUserTokens(userId);

    return refreshTokenRepository.save(refreshToken);
  }

  /**
   * Verifies if the refresh token has expired
   *
   * @param token the token to verify
   * @return the token if valid
   * @throws TokenRefreshException if the token has expired
   */
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token == null) {
      throw new TokenRefreshException("", "Refresh token is null");
    }
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
          token.getToken(), "Refresh token was expired. Please make a new signin request");
    }
    if (token.isRevoked()) {
      throw new TokenRefreshException(
          token.getToken(), "Refresh token was revoked. Please sign in again");
    }
    return token;
  }

  /**
   * Deletes all refresh tokens for a specific user
   *
   * @param userId the user ID
   */
  @Transactional
  public void deleteByUserId(Integer userId) {
    refreshTokenRepository.revokeAllUserTokens(userId);
  }

  /** Deletes all expired refresh tokens */
  @Transactional
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteExpiredTokens();
  }
}
