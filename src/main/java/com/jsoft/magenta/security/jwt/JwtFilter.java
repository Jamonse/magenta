package com.jsoft.magenta.security.jwt;

import com.google.common.base.Strings;
import com.jsoft.magenta.util.AppConstants;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtManager jwtManager;

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
      FilterChain filterChain) throws ServletException, IOException {
    String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    // Check if there is an authorization header in the request
    if (Strings.isNullOrEmpty(authHeader) || !authHeader
        .startsWith(jwtManager.getTokenPrefix())) { // No
      // Authorization header
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }
    // Extract token from authorization header
    String token = authHeader.replace(jwtManager.getTokenPrefix(), "");

    try {
      if (!Strings.isNullOrEmpty(token) && jwtManager
          .validateToken(token)) { // Token is valid - extract
        // authentication from it
        Authentication authentication = jwtManager.getAuthentication(token);
        // initialize security context holder with extracted authentications
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
      }
    } catch (JwtException e) { // Token is not valid - send an error in the response
      SecurityContextHolder.clearContext();
      httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
      httpServletResponse.getWriter().write(AppConstants.INVALID_TOKEN);
      log.info("An invalid token was caught in filter chain");
      return;
    } // Continue filter chain
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }
}
