package com.ayushrawat.auth.security;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.entity.UserRole;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record SecureUser(User user) implements UserDetails {

  @Override
  public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> roles = new ArrayList<>();
    for (UserRole role : UserRole.fromBitmask(user.getRole())) {
      roles.add(new SimpleGrantedAuthority(role.name()));
    }
    return roles;
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  @Override
  public @NonNull String getUsername() {
    return user.getUsername();
  }

}
