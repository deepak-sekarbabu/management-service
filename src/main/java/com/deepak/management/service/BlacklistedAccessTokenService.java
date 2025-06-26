package com.deepak.management.service;

import java.time.Instant;

public interface BlacklistedAccessTokenService {
  void blacklistToken(String token, Instant expiry);

  boolean isTokenBlacklisted(String token);

  void removeExpiredTokens();
}
