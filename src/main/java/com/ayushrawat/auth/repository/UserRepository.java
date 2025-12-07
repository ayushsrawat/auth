package com.ayushrawat.auth.repository;

import com.ayushrawat.auth.entity.User;

import java.util.Optional;

public interface UserRepository {

  User save(User user);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findById(Integer id);

}
