package com.ayushrawat.auth.service.impl;

import com.ayushrawat.auth.config.RabbitMQConfig;
import com.ayushrawat.auth.payload.event.UserRegisteredEvent;
import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.entity.UserRole;
import com.ayushrawat.auth.mapper.UserMapper;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
  private final RabbitTemplate rabbitTemplate;

  public User registerUser(UserDTO userDTO) {
    Assert.isTrue(userRepository.findByUsername(userDTO.getUsername()).isEmpty(), "Username already taken.");
    Assert.isTrue(userRepository.findByEmail(userDTO.getEmail()).isEmpty(), "Email already registered");
    try {
      User user = userMapper.toEntity(userDTO);
      user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
      user.setCreatedAt(LocalDateTime.now());
      user.setRole(UserRole.ROLE_USER.getBit());
      user.setDeleted(false);

      logger.info("Saving user : {} with roles {}", userDTO.getUsername(), UserRole.fromBitmask(user.getRole()));
      User userWithId = userRepository.save(user);
      try {
        String routingKey = "user.registered";
        logger.info("Sending event [{}] to RabbitMQ", routingKey);
        UserRegisteredEvent event = new UserRegisteredEvent(userWithId.getId(), userWithId.getUsername(), userWithId.getEmail());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
      } catch (AmqpException ae) {
        logger.error("Error publishing user register event");
      }
      return userWithId;
    } catch (DataIntegrityViolationException e) {
      throw new IllegalArgumentException("Error registering the user. Please ensure all the details are valid.");
    }
  }

}
