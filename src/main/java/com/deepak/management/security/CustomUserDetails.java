package com.deepak.management.security;

import com.deepak.management.model.auth.User;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {
  private final User user;
  private final List<Integer> clinicIds;

  public CustomUserDetails(User user) {
    this.user = user;
    this.clinicIds = user.getClinicIds() != null ? user.getClinicIds() : List.of();
  }

  public CustomUserDetails(UserDetails userDetails, List<Integer> clinicIds) {
    if (!(userDetails instanceof CustomUserDetails)) {
      throw new IllegalArgumentException("UserDetails must be an instance of CustomUserDetails");
    }
    this.user = ((CustomUserDetails) userDetails).getUser();
    this.clinicIds = Objects.requireNonNullElse(clinicIds, List.of());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return user.getLockedUntil() == null
        || user.getLockedUntil().isBefore(java.time.LocalDateTime.now());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return Boolean.TRUE.equals(user.getIsActive());
  }

  public Long getId() {
    return user.getId() != null ? user.getId().longValue() : null;
  }
}
