package com.ayushrawat.auth.repository;

import com.ayushrawat.auth.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Integer> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

}
