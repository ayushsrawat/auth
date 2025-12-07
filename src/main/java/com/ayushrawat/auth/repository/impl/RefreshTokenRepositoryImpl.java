package com.ayushrawat.auth.repository.impl;

import com.ayushrawat.auth.entity.RefreshToken;
import com.ayushrawat.auth.jooq.tables.records.RefreshTokensRecord;
import com.ayushrawat.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.ayushrawat.auth.jooq.Tables.REFRESH_TOKENS;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final DSLContext dsl;

  @Override
  public RefreshToken save(RefreshToken r) {
    return dsl
        .insertInto(REFRESH_TOKENS)
        .set(REFRESH_TOKENS.REFRESH_TOKEN, r.token())
        .set(REFRESH_TOKENS.USER_ID, r.userId())
        .set(REFRESH_TOKENS.EXPIRY_DATE, r.expiryDate())
        .returning()
        .fetchOne(this::map);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    Optional<RefreshTokensRecord> refreshTokensRecord = dsl
        .selectFrom(REFRESH_TOKENS)
        .where(REFRESH_TOKENS.REFRESH_TOKEN.eq(token))
        .fetchOptional();
    return refreshTokensRecord.map(this::map);
  }

  @Override
  public int deleteByUser(Integer userId) {
    return dsl.transactionResult(_ -> dsl
        .deleteFrom(REFRESH_TOKENS)
        .where(REFRESH_TOKENS.USER_ID.eq(userId))
        .execute());
  }

  @Override
  public int delete(RefreshToken r) {
    return dsl.transactionResult(_ -> dsl
        .deleteFrom(REFRESH_TOKENS)
        .where(REFRESH_TOKENS.ID.eq(r.id()))
        .returning(REFRESH_TOKENS.ID)
        .fetchOneInto(Integer.class));
  }

  private RefreshToken map(RefreshTokensRecord r) {
    return RefreshToken
        .builder()
        .id(r.getId())
        .token(r.getRefreshToken())
        .userId(r.getUserId())
        .expiryDate(r.getExpiryDate())
        .build();
  }

}
