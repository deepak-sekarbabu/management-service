package com.deepak.management.service.impl;

import com.deepak.management.model.auth.BlacklistedAccessToken;
import com.deepak.management.repository.BlacklistedAccessTokenRepository;
import com.deepak.management.service.BlacklistedAccessTokenService;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class BlacklistedAccessTokenServiceImpl implements BlacklistedAccessTokenService {
  private final BlacklistedAccessTokenRepository repository;

  public BlacklistedAccessTokenServiceImpl(BlacklistedAccessTokenRepository repository) {
    this.repository = repository;
  }

  @Override
  public void blacklistToken(String token, Instant expiry) {
    if (!repository.findByToken(token).isPresent()) {
      repository.save(new BlacklistedAccessToken(token, expiry));
    }
  }

  @Override
  public boolean isTokenBlacklisted(String token) {
    return repository.findByToken(token).isPresent();
  }

  @Override
  public void removeExpiredTokens() {
    repository.deleteByExpiryDateBefore(Instant.now());
  }
}
