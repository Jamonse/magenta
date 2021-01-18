package com.jsoft.magenta.security.jwt;

import com.jsoft.magenta.security.CustomUserDetailsService;
import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import com.jsoft.magenta.security.model.Privilege;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtManager
{
    @Value("${application.jwt.secret}")
    private String tokenSecret;

    @Value("${application.jwt.tokenPrefix:Bearer}")
    private String tokenPrefix;

    @Value("${application.jwt.expiration-length:1}")
    private int tokenExpirationLengthInDays;

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    private void init()
    { // Encode the token secret with base 64 before encoding it in JWT construction
        tokenSecret = Base64.getEncoder().encodeToString(tokenSecret.getBytes());
    }

    public String createToken(String email, Set<Privilege> privileges)
    {
        Claims claims = Jwts.claims().setSubject(email); // Set user email as token subject
        Set<CustomGrantedAuthority> grantedAuthorities = privileges.stream() // Create authorities from privileges
                .map(privilege -> new CustomGrantedAuthority(privilege))
                .collect(Collectors.toSet());
        claims.put("auth", grantedAuthorities); // Set authorities to claims

        Date now = new Date(); // Create issue date and expiration date
        Date expirationDate = java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationLengthInDays));
        // Build and return the token
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    public Authentication getAuthentication(String token)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String getUsername(String token)
    {
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token)
    {
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException |
                UnsupportedJwtException |
                MalformedJwtException |
                SignatureException |
                IllegalArgumentException e) {
            throw new JwtException("Token is either invalid or expired");
        }
    }

    public String getTokenPrefix()
    {
        return tokenPrefix;
    }
}
