package com.ayushrawat.auth.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

  private String username;
  private String email;
  private String password;

}
