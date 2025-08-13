package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.dto.LoginRequest;
import com.ayushrawat.auth.dto.LoginResponse;
import com.ayushrawat.auth.dto.UserDTO;
import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.entity.UserRole;
import com.ayushrawat.auth.mapper.UserMapper;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.service.AuthService;
import com.ayushrawat.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final UserMapper userMapper;

  @Override
  public User registerUser(UserDTO userDTO) {
    Assert.isTrue(userRepository.findByUsername(userDTO.getUsername()).isEmpty(), "Username already taken.");
    try {
      User user = userMapper.toEntity(userDTO);
      user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
      user.setCreatedAt(LocalDateTime.now());
      user.setRole(UserRole.ROLE_USER.getBit());

      logger.info("saving user : {} with roles {}", userDTO.getUsername(), UserRole.fromBitmask(user.getRole()));
      return userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Error registering the user. Please ensure all the details are valid.");
    }
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Optional<User> user = userRepository.findByUsername(request.getUsername());
    if (user.isPresent()) {
      if (passwordEncoder.matches(request.getPassword(), user.get().getPasswordHash())) {
        logger.info("creating jwt token for user : {}", request.getUsername());
        return new LoginResponse(user.get().getUsername(), jwtUtil.generateToken(user.get()));
      }
    }
    return null;
  }

}
