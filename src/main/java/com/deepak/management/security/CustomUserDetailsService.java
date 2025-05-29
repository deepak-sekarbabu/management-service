package com.deepak.management.security;

import com.deepak.management.model.auth.User;
import com.deepak.management.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));
    if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 10) {
      LocalDateTime now = LocalDateTime.now();
      if (user.getLockedUntil() == null || user.getLockedUntil().isBefore(now)) {
        user.setLockedUntil(now.plusMinutes(10));
        userRepository.save(user);
      }
      throw new LockedException(
          "Account locked due to too many failed login attempts. Try again after: "
              + user.getLockedUntil());
    }
    return new CustomUserDetails(user);
  }
}
