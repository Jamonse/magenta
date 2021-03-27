package com.jsoft.magenta.security.service;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.dao.RefreshTokenRepository;
import com.jsoft.magenta.security.model.RefreshToken;
import com.jsoft.magenta.util.AppConstants;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${application.refresh.expiration-length:1}")
  private int refreshTokenExpirationTime;

  private final RefreshTokenRepository refreshTokenRepository;

  public String createRefreshToken(String userName) {
    String token = UUID.randomUUID().toString();
    RefreshToken refreshToken = new RefreshToken(token,
        LocalDateTime.now().plusWeeks(refreshTokenExpirationTime), userName);
    this.refreshTokenRepository.save(refreshToken);
    return token;
  }

  public RefreshToken validateToken(String token) { // Search for token in DB
    RefreshToken refreshToken = refreshTokenRepository.findById(token)
        .orElseThrow(
            () -> new NoSuchElementException(AppConstants.INVALID_TOKEN)); // Token not found
    LocalDateTime expirationTime = refreshToken.getExpiresAt();
    if (expirationTime
        .isBefore(LocalDateTime.now())) { // Token found but expired - remove it and throw exception
      this.refreshTokenRepository.deleteById(refreshToken.getToken());
      throw new DateTimeException(AppConstants.INVALID_TOKEN);
    }
    return refreshToken;
  }

  public void removeRefreshTokenIfExist(String refreshToken) {
    boolean exist = this.refreshTokenRepository.existsByToken(refreshToken);
    if (exist) {
      this.refreshTokenRepository.deleteByToken(refreshToken);
    }
  }

}
