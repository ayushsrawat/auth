package com.ayushrawat.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true, fluent = true)
@Getter
@Setter
@Builder
public class RefreshToken {

  private Integer id;
  private String token;
  private Integer userId;
  private LocalDateTime expiryDate;

}
