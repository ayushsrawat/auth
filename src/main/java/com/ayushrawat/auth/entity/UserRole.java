package com.ayushrawat.auth.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum UserRole {

  ROLE_ADMIN(1),
  ROLE_MANAGEMENT(1 << 1),
  ROLE_USER(1 << 2);
  private final int bit;

  UserRole(int bit) {
    this.bit = bit;
  }

  public static List<UserRole> fromBitmask(int bitmask) {
    return Arrays.stream(UserRole.values())
      .filter(role -> (bitmask & role.getBit()) != 0)
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unused")
  public static int toBitmask(List<UserRole> roles) {
    return roles.stream().mapToInt(UserRole::getBit).reduce(0, (a, b) -> a | b);
  }

}
