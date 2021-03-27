package com.jsoft.magenta.security.dao;

import com.jsoft.magenta.security.model.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

  @Modifying
  @Transactional
  void deleteByToken(String refreshToken);

  @Modifying
  @Transactional
  @Query("delete from RefreshToken token where token.expiresAt < CURRENT_TIMESTAMP")
  void removeExpiredTokens();

  boolean existsByToken(String refreshToken);
}
