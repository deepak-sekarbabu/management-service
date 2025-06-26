package com.deepak.management.repository;

import com.deepak.management.model.auth.BlacklistedAccessToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedAccessTokenRepository
    extends JpaRepository<BlacklistedAccessToken, Long> {
  Optional<BlacklistedAccessToken> findByToken(String token);

  void deleteByExpiryDateBefore(java.time.Instant now);
}
