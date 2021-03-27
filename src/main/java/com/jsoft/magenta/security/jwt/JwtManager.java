package com.jsoft.magenta.security.jwt;

import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.service.CustomUserDetailsService;
import com.jsoft.magenta.util.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtManager {

  private KeyStore keyStore;

  @Value("${application.jwt.secret}")
  private String tokenSecret;

  @Value("${application.jwt.tokenPrefix:Bearer}")
  private String tokenPrefix;

  @Value("${application.jwt.expiration-length:1}")
  private int tokenExpirationLengthInMinutes;

  private final CustomUserDetailsService userDetailsService;

  @PostConstruct
  private void init() {
    try { // Load keystore from keystore file
      keyStore = KeyStore.getInstance("JKS");
      InputStream inputStream = getClass().getResourceAsStream("/magenta.jks");
      keyStore.load(inputStream, tokenSecret.toCharArray());
    } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
      log.error(AppConstants.SECURITY_MESSAGE);
    }
  }

  public String createToken(String email, Set<Privilege> privileges) {
    Claims claims = Jwts.claims().setSubject(email); // Set user email as token subject
    Set<CustomGrantedAuthority> grantedAuthorities = privileges
        .stream() // Create authorities from privileges
        .map(privilege -> new CustomGrantedAuthority(privilege))
        .collect(Collectors.toSet());
    claims.put("auth", grantedAuthorities); // Set authorities to claims

    Date issuedAt = Date.from(Instant.now());
    Date expirationDate = Date.from(
        Instant.now().plusSeconds(tokenExpirationLengthInMinutes * AppConstants.SECONDS_IN_MINUTE));
    // Build and return the token
    return Jwts.builder()
        .addClaims(claims)
        .setIssuedAt(issuedAt)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.RS512, getPrivateKey())
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  private String getUsername(String token) {
    return Jwts.parser()
        .setSigningKey(getPublicKey())
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException |
        UnsupportedJwtException |
        MalformedJwtException |
        SignatureException |
        IllegalArgumentException e) {
      throw new JwtException("Token is either invalid or expired");
    }
  }

  public String getTokenPrefix() {
    return tokenPrefix;
  }

  private PrivateKey getPrivateKey() {
    try {
      return (PrivateKey) keyStore.getKey(AppConstants.ALIAS, tokenSecret.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      log.error("Error during pk loading");
    }
    throw new IllegalStateException("An uncaught exception was raised during keystore operation");
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate(AppConstants.ALIAS).getPublicKey();
    } catch (KeyStoreException e) {
      log.error("Error during public key loading");
    }
    throw new IllegalStateException("An uncaught exception was raised during keystore operation");
  }
}
