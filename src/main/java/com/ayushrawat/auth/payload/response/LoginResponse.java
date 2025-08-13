package com.ayushrawat.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

  private String username;
  private String accessToken;
  private String refreshToken;

}
