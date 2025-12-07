package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.entity.UserRole;
import com.ayushrawat.auth.mapper.UserMapper;
import com.ayushrawat.auth.payload.event.UserRegisteredEvent;
import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Value("${auth.user.rmq.exchange}")
  private String userExchange;

  @Value("${auth.user.rmq.keys.registered}")
  private String userRegisteredRegisterKey;

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RabbitTemplate rabbitTemplate;

  public User registerUser(UserDTO userDTO) {
    Assert.isTrue(userRepository.findByUsername(userDTO.getUsername()).isEmpty(), "Username already taken.");
    Assert.isTrue(userRepository.findByEmail(userDTO.getEmail()).isEmpty(), "Email already registered");
    try {
      User user = userMapper.toEntity(userDTO);
      user.passwordHash(passwordEncoder.encode(userDTO.getPassword()));
      user.createdAt(LocalDateTime.now());
      user.role(UserRole.ROLE_USER.getBit());
      user.deleted(false);

      logger.info("Saving user : {} with roles {}", userDTO.getUsername(), UserRole.fromBitmask(user.role()));
      User userWithId = userRepository.save(user);

      try {
        logger.info("Sending event {}:[{}] to RabbitMQ",userWithId.username(),  userRegisteredRegisterKey);
        UserRegisteredEvent event = new UserRegisteredEvent(userWithId.id(), userWithId.username(), userWithId.email(), userWithId.role());
        rabbitTemplate.convertAndSend(userExchange, userRegisteredRegisterKey, event);
      } catch (Exception ae) {
        logger.error("Error publishing user register event: routing key [{}] \n {} ", userRegisteredRegisterKey, ae.getMessage());
      }
      return userWithId;
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Error registering the user. Please ensure all the details are valid.");
    }
  }

}
