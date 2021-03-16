package com.jsoft.magenta.security.service;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.dao.RefreshTokenRepository;
import com.jsoft.magenta.security.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String userName) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(token, LocalDateTime.now(), userName);
        this.refreshTokenRepository.save(refreshToken);
        return token;
    }

    public void validateToken(String token) { // Search for token in DB
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Invalid token")); // Token not found
        LocalDateTime expirationTime = refreshToken.getExpiresAt();
        if (expirationTime.isBefore(LocalDateTime.now())) { // Token found but expired - remove it and throw exception
            this.refreshTokenRepository.deleteById(refreshToken.getToken());
            throw new DateTimeException("Invalid token");
        }

    }

    public void removeRefreshTokenIfExist(String refreshToken) {
        boolean exist = this.refreshTokenRepository.existsByToken(refreshToken);
        if (exist)
            this.refreshTokenRepository.deleteByToken(refreshToken);
    }

}
