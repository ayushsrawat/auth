package com.ayushrawat.auth.controller;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.payload.request.LoginRequest;
import com.ayushrawat.auth.payload.request.TokenRefreshRequest;
import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.payload.response.TokenRefreshResponse;
import com.ayushrawat.auth.service.AuthService;
import com.ayushrawat.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
    try {
      User user = userService.registerUser(userDTO);
      return ResponseEntity.ok(user);
    } catch (IllegalArgumentException iae) {
      return ResponseEntity.badRequest().body(iae.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    var loginResponse = authService.login(loginRequest);
    if (loginResponse == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
    TokenRefreshResponse response = authService.refreshToken(tokenRefreshRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logoutUser() {
    authService.logoutUser();
    return ResponseEntity.ok("Logged out successfully");
  }

}
