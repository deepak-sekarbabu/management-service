package com.deepak.management.controller;

import com.deepak.management.model.auth.LoginRequest;
import com.deepak.management.model.auth.LoginResponse;
import com.deepak.management.model.auth.TokenValidationRequest;
import com.deepak.management.model.auth.TokenValidationResponse;
import com.deepak.management.repository.UserRepository;
import com.deepak.management.security.CustomUserDetails;
import com.deepak.management.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "APIs for managing JWT tokens")
public class AuthController {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Autowired private UserRepository userRepository;

  @Operation(
      summary = "User Login",
      description = "Authenticates the user and returns a JWT token on successful login")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
      })
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Get the authenticated user details
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // Update last_login_at
    userRepository
        .findByUsername(loginRequest.getUsername())
        .ifPresent(
            user -> {
              user.setLastLoginAt(LocalDateTime.now());
              userRepository.save(user);
            });

    // Generate token with username, role and clinicIds
    String jwt =
        jwtTokenProvider.generateToken(
            loginRequest.getUsername(),
            userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""),
            userDetails.getClinicIds());

    return ResponseEntity.ok(new LoginResponse(jwt));
  }

  @Operation(
      summary = "Validate JWT Token",
      description = "Validates the provided JWT token and returns the associated username if valid")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token is valid",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TokenValidationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Token is invalid", content = @Content)
      })
  @PostMapping("/validate")
  public ResponseEntity<TokenValidationResponse> validateToken(
      @RequestBody TokenValidationRequest tokenRequest) {
    boolean valid = jwtTokenProvider.validateToken(tokenRequest.getToken());
    if (valid) {
      String username = jwtTokenProvider.getUsernameFromJWT(tokenRequest.getToken());
      String role = jwtTokenProvider.getRoleFromJWT(tokenRequest.getToken());
      List<Integer> clinicIds = jwtTokenProvider.getClinicIdsFromJWT(tokenRequest.getToken());
      return ResponseEntity.ok(new TokenValidationResponse(true, username, role, clinicIds));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new TokenValidationResponse(false, null, null, null));
    }
  }
}
