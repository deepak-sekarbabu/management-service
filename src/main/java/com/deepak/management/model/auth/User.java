package com.deepak.management.model.auth;

import com.deepak.management.converter.IntegerListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 50)
  @NotBlank
  @Size(max = 50)
  private String username;

  @NotBlank
  @Size(min = 8, max = 255)
  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @NotBlank
  @Email
  @Size(max = 100)
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Size(max = 10)
  @Column(name = "phone_number", length = 10)
  private String phoneNumber;

  @Column(nullable = false, length = 10)
  @NotBlank
  @Size(max = 10)
  private String role;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = false;

  @Builder.Default
  @Column(name = "failed_login_attempts", nullable = false)
  private Integer failedLoginAttempts = 0;

  @Column(name = "locked_until")
  private LocalDateTime lockedUntil;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "clinic_id")
  @Convert(converter = IntegerListConverter.class)
  private List<Integer> clinicIds;

  @PrePersist
  protected void onCreate() {
    createdAt = updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
