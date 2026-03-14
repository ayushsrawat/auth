package com.ayushrawat.auth.service;

import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.payload.response.RegisterUserResponse;

public interface UserService {

  RegisterUserResponse registerUser(UserDTO user);

}
