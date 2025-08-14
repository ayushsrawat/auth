package com.ayushrawat.auth;

import com.ayushrawat.auth.payload.request.LoginRequest;
import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthApplicationTests {

  private static final Logger logger = LoggerFactory.getLogger(AuthApplicationTests.class);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void contextLoads() {
    assertThat(mockMvc).isNotNull();
  }

  @Test
  void shouldRegisterUserSuccessfully() throws Exception {
    UserDTO userDTO = new UserDTO("testuser", "test@example.com", "password123");
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDTO)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.username").value("testuser"));

    assertThat(userRepository.findByUsername("testuser")).isPresent();
  }

  @Test
  void shouldFailRegistrationWhenUsernameExists() throws Exception {
    // Arrange: Create a user first
    UserDTO existingUser = new UserDTO("existinguser", "exist@example.com", "password123");
    mockMvc.perform(post("/api/v1/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(existingUser)));

    // Act & Assert: Try to register again with the same username
    mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(existingUser)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void shouldLoginSuccessfullyWithCorrectCredentials() throws Exception {
    // Arrange: Register a user first
    UserDTO userDTO = new UserDTO("loginuser", "login@example.com", "password123");
    mockMvc.perform(post("/api/v1/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(userDTO)));

    LoginRequest loginRequest = new LoginRequest("loginuser", "password123");

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.accessToken").isString())
      .andExpect(jsonPath("$.username").value("loginuser"));
  }

  @Test
  void shouldFailLoginWithIncorrectPassword() throws Exception {
    // Arrange: Register a user first
    UserDTO userDTO = new UserDTO("loginuser2", "login@example.com", "password123");
    mockMvc.perform(post("/api/v1/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(userDTO)));

    LoginRequest loginRequest = new LoginRequest("loginuser2", "wrongpassword");

    // Act & Assert
    mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAccessProtectedEndpointWithValidToken() throws Exception {
    // Arrange: Register and login to get a token
    UserDTO userDTO = new UserDTO("authtest", "login@example.com", "password123");
    mockMvc.perform(post("/api/v1/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(userDTO)));

    LoginRequest loginRequest = new LoginRequest("authtest", "password123");

    MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andReturn();

    String responseBody = loginResult.getResponse().getContentAsString();
    JsonNode responseJson = objectMapper.readTree(responseBody);
    String token = responseJson.get("accessToken").asText();

    logger.info("Created token: {}", token);

    // Act & Assert
		mockMvc.perform(get("/api/v1/test/hello")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk());
  }

  @Test
  void shouldFailToAccessProtectedEndpointWithoutToken() throws Exception {
    mockMvc.perform(get("/api/v1/test/hello"))
      .andExpect(status().isForbidden()); // Or isUnauthorized(), depending on config
  }
}