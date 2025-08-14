package com.ayushrawat.auth.service;

import com.ayushrawat.auth.payload.request.LoginRequest;
import com.ayushrawat.auth.payload.request.TokenRefreshRequest;
import com.ayushrawat.auth.payload.response.LoginResponse;
import com.ayushrawat.auth.payload.response.TokenRefreshResponse;

public interface AuthService {

  LoginResponse login(LoginRequest loginRequest);

  TokenRefreshResponse refreshToken(TokenRefreshRequest tokenRefreshRequest);

  void logoutUser();

}
