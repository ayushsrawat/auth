package com.ayushrawat.auth.service;

import com.ayushrawat.auth.dto.LoginRequest;
import com.ayushrawat.auth.dto.LoginResponse;
import com.ayushrawat.auth.dto.UserDTO;
import com.ayushrawat.auth.entity.User;

public interface AuthService {

  User registerUser(UserDTO user);

  LoginResponse login(LoginRequest loginRequest);

}
