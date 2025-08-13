package com.ayushrawat.auth.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenRefreshRequest {

  private String refreshToken;

}
