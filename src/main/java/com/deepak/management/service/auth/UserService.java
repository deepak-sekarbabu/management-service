package com.deepak.management.service.auth;

import com.deepak.management.model.auth.User;
import com.deepak.management.model.auth.UserRegistrationRequest;
import com.deepak.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public User registerUser(UserRegistrationRequest request) {
    User user =
        User.builder()
            .username(request.getUsername())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .role(request.getRole())
            .isActive(true)
            .build();
    return userRepository.save(user);
  }

  @Transactional
  public void updatePassword(Integer userId, String newPassword) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(Integer userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }
    userRepository.deleteById(userId);
  }
}
