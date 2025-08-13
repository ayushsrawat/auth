package com.ayushrawat.auth.service;

import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.entity.User;

public interface UserService {

  User registerUser(UserDTO user);

}
