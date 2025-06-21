package com.deepak.management.model.auth;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean revoked;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
