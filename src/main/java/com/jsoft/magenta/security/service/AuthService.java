package com.jsoft.magenta.security.service;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.jwt.JwtManager;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.model.RefreshToken;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  private final RefreshTokenService refreshTokenService;
  private final SecurityService securityService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final JwtManager jwtManager;

  public String generateRefreshedJwt(String refreshToken) { // Verify refresh token
    RefreshToken validatedToken = this.refreshTokenService.validateToken(refreshToken);
    // Fetch user date and create new JWT
    User user = findUser(validatedToken.getEmail());
    String userName = user.getEmail();
    Set<Privilege> privileges = user.getPrivileges();
    return this.jwtManager.createToken(userName, privileges);
  }

  public void logout(String refreshToken) { // Remove refresh token if exists
    this.refreshTokenService.removeRefreshTokenIfExist(refreshToken);
    SecurityContextHolder.clearContext(); // Clear security context
  }

  public boolean authenticate(String passwordToMatch) {
    User user = securityService.currentUser();
    String userPassword = user.getPassword();
    return passwordEncoder.matches(passwordToMatch, userPassword);
  }

  public User updatePassword(Long userId, String newPassword) {
    User user = findUser(userId);
    user.setPassword(passwordEncoder.encode(newPassword));
    return this.userRepository.save(user);
  }

  private User findUser(Long userId) {
    return this.userRepository
        .findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
  }

  private User findUser(String email) {
    return this.userRepository
        .findByEmail(email)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
  }
}
