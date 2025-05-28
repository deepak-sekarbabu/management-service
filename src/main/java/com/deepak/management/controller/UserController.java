package com.deepak.management.controller;

import com.deepak.management.model.auth.*;
import com.deepak.management.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account with the provided registration details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
      })
  @PostMapping("/register")
  public ResponseEntity<User> registerUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "User registration details",
              required = true,
              content = @Content(schema = @Schema(implementation = UserRegistrationRequest.class)))
          @Valid
          @RequestBody
          UserRegistrationRequest request) {
    User user = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @Operation(
      summary = "Update user password",
      description = "Updates the password for the user with the specified ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
      })
  @PutMapping("/{id}/password")
  public ResponseEntity<Void> updatePassword(
      @Parameter(description = "ID of the user to update", required = true) @PathVariable
          Integer id,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "New password details",
              required = true,
              content =
                  @Content(schema = @Schema(implementation = UserPasswordUpdateRequest.class)))
          @Valid
          @RequestBody
          UserPasswordUpdateRequest request) {
    userService.updatePassword(id, request.newPassword());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Delete a user", description = "Deletes the user with the specified ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(description = "ID of the user to delete", required = true) @PathVariable
          Integer id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
