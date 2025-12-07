package com.ayushrawat.auth.security;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecureUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    Optional<User> optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isEmpty()) {
      throw new UsernameNotFoundException("user with username " + username + " not found");
    }
    return new SecureUser(optionalUser.get());
  }

}
