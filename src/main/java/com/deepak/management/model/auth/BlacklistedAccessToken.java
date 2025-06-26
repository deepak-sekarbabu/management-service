package com.deepak.management.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;

@Entity
@Table(name = "blacklisted_access_tokens")
@Data
public class BlacklistedAccessToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 512)
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;

  public BlacklistedAccessToken() {}

  public BlacklistedAccessToken(String token, Instant expiryDate) {
    this.token = token;
    this.expiryDate = expiryDate;
  }
}
