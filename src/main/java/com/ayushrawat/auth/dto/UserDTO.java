package com.ayushrawat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

  private String username;
  private String firstName;
  private String lastName;
  private String password;
  private Date   dob;
  private String phone;
  private String email;
  private String address;

}
