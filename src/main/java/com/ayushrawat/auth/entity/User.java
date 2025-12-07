package com.ayushrawat.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true, fluent = true)
@Setter
@Getter
@Builder
public class User {

  private Integer id;
  private String username;
  private String email;
  private String passwordHash;
  private Integer role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean deleted;

}