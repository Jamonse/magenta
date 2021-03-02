package com.jsoft.magenta.security.dao;

import com.jsoft.magenta.security.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>
{
    @Modifying
    @Transactional
    void deleteByUserName(String userName);

    @Modifying
    @Transactional
    @Query("delete token from RefreshToken token where token.expiresAt < CURRENT_TIMESTAMP")
    void removeExpiredTokens();

    boolean existsByUserName(String userName);

    Optional<RefreshToken> findByToken(String token);
}
