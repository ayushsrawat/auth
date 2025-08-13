package com.ayushrawat.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TokenRefreshResponse {

  private String userName;
  private String accessToken;
  private String refreshToken;

}
