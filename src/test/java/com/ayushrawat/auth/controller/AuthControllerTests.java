package com.ayushrawat.auth.controller;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.payload.request.LoginRequest;
import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.payload.response.LoginResponse;
import com.ayushrawat.auth.repository.UserRepository;
import com.ayushrawat.auth.service.AuthService;
import com.ayushrawat.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private String refreshToken;
  @Autowired
  private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        UserDTO userDTO = new UserDTO("refresh_tester", "login@example.com", "password");
        userService.registerUser(userDTO);

        LoginRequest loginRequest = new LoginRequest("refresh_tester", "password");
        LoginResponse loginResponse = authService.login(loginRequest);
        refreshToken = loginResponse.getRefreshToken();
    }

    @AfterEach
    void reset() {
        Optional<User> user = userRepository.findByUsername("refresh_tester");
        if (user.isEmpty()) throw new RuntimeException("refresh_tester user not found");
        userRepository.deleteById(user.get().getId());
    }

    @Test
    void testRefreshToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    void testLogout() throws Exception {
        // First, login to get a valid token
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("refresh_tester", "password"))))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asText();

        // Then, logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Logged out successfully"));

        // Finally, try to use the refresh token again, it should fail
        mockMvc.perform(post("/api/v1/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isBadRequest());
    }
}
