package com.deepak.management.security;

import com.deepak.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureListener.class);
  private final UserRepository userRepository;

  @Override
  public void onApplicationEvent(
      @SuppressWarnings("null") AuthenticationFailureBadCredentialsEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    if (principal instanceof String username) {
      userRepository
          .findByUsername(username)
          .ifPresent(
              user -> {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                userRepository.save(user);
                LOGGER.warn("Incremented failed login attempts for user: {}", username);
              });
    }
  }
}
