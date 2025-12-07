package com.ayushrawat.auth.repository.impl;

import com.ayushrawat.auth.entity.User;
import com.ayushrawat.auth.jooq.tables.records.UsersRecord;
import com.ayushrawat.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.ayushrawat.auth.jooq.Tables.USERS;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final DSLContext dsl;

  @Override
  public User save(User user) {
    return dsl
        .insertInto(USERS)
        .set(USERS.USERNAME, user.username())
        .set(USERS.EMAIL, user.email())
        .set(USERS.PASSWORD_HASH, user.passwordHash())
        .set(USERS.USER_ROLE, user.role())
        .returning()
        .fetchOne(this::map);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    Optional<UsersRecord> user = dsl.selectFrom(USERS).where(USERS.USERNAME.eq(username)).fetchOptional();
    return user.map(this::map);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    Optional<UsersRecord> user = dsl.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOptional();
    return user.map(this::map);
  }

  @Override
  public Optional<User> findById(Integer id) {
    Optional<UsersRecord> user = dsl.selectFrom(USERS).where(USERS.ID.eq(id)).fetchOptional();
    return user.map(this::map);
  }

  private User map(UsersRecord u) {
    return User
        .builder()
        .id(u.getId())
        .username(u.getUsername())
        .email(u.getEmail())
        .passwordHash(u.getPasswordHash())
        .role(u.getUserRole())
        .createdAt(u.getCreatedAt())
        .updatedAt(u.getUpdatedAt())
        .deleted(u.getDeleted())
        .build();
  }

}
