package com.deepak.management.controller;

import com.deepak.management.exception.TokenRefreshException;
import com.deepak.management.model.auth.*;
import com.deepak.management.model.auth.RefreshToken;
import com.deepak.management.repository.UserRepository;
import com.deepak.management.security.CustomUserDetails;
import com.deepak.management.security.JwtTokenProvider;
import com.deepak.management.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "APIs for authentication and token management")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserRepository userRepository;
  private final RefreshTokenService refreshTokenService;

  @Operation(
      summary = "User Login",
      description =
          "Authenticates the user and returns access and refresh tokens on successful login")
  @ApiResponses({
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
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // Update last login time
    userRepository
        .findByUsername(loginRequest.getUsername())
        .ifPresent(
            user -> {
              user.setLastLoginAt(LocalDateTime.now());
              userRepository.save(user);
            });

    // Generate tokens
    String accessToken =
        tokenProvider.generateAccessToken(
            userDetails.getUsername(),
            userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"),
            userDetails.getClinicIds());

    // Convert Long to Integer for user ID
    Integer userId = userDetails.getId().intValue();
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);

    // Build response
    LoginResponse response = new LoginResponse();
    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken.getToken());
    response.setTokenType("Bearer");
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Refresh Access Token",
      description = "Generates a new access token using a valid refresh token")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully refreshed token",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenRefreshResponse.class))),
    @ApiResponse(responseCode = "401", description = "Invalid refresh token", content = @Content)
  })
  @PostMapping("/refresh-token")
  public ResponseEntity<TokenRefreshResponse> refreshToken(
      @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService
        .findByToken(requestRefreshToken)
        .map(
            refreshToken -> {
              RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshToken);
              return userRepository
                  .findById(verifiedToken.getUserId())
                  .map(
                      user -> {
                        // Generate new access token
                        String role =
                            user.getRole(); // Assuming getRole() returns the role as String
                        String accessToken =
                            tokenProvider.generateAccessToken(
                                user.getUsername(), role, user.getClinicIds());

                        // Generate new refresh token (optional: rotate refresh token)
                        RefreshToken newRefreshToken =
                            refreshTokenService.createRefreshToken(user.getId());

                        return ResponseEntity.ok(
                            new TokenRefreshResponse(accessToken, newRefreshToken.getToken()));
                      })
                  .orElseThrow(
                      () -> new TokenRefreshException(requestRefreshToken, "User not found"));
            })
        .orElseThrow(
            () ->
                new TokenRefreshException(
                    requestRefreshToken, "Refresh token not found or expired"));
  }

  @Operation(summary = "Logout", description = "Invalidates the current user's refresh tokens")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully logged out"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
      Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
      refreshTokenService.deleteByUserId(userId.intValue());
    }
    return ResponseEntity.ok().build();
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
    boolean valid = tokenProvider.validateToken(tokenRequest.getToken());
    if (valid) {
      String username = tokenProvider.getUsernameFromToken(tokenRequest.getToken());
      String role =
          tokenProvider.getClaimFromToken(
              tokenRequest.getToken(), claims -> claims.get("role", String.class));
      @SuppressWarnings("unchecked")
      List<Integer> clinicIds =
          (List<Integer>)
              tokenProvider.getClaimFromToken(
                  tokenRequest.getToken(), claims -> claims.get("clinicIds"));
      return ResponseEntity.ok(new TokenValidationResponse(true, username, role, clinicIds));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new TokenValidationResponse(false, null, null, null));
    }
  }
}
