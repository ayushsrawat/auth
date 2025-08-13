package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.entity.UserRole;
import com.ayushrawat.auth.mapper.UserMapper;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

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

}
