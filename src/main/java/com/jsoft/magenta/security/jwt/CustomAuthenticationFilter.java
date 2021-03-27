package com.jsoft.magenta.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoft.magenta.exceptions.MagentaException;
import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import com.jsoft.magenta.security.model.LoginResponse;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.model.UserPrincipal;
import com.jsoft.magenta.security.payload.UsernamePasswordRequest;
import com.jsoft.magenta.security.service.RefreshTokenService;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.StringUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final JwtManager jwtManager;

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try { // Attempt authentication by reading username and password from request
      UsernamePasswordRequest usernamePasswordRequest =
          new ObjectMapper().readValue(request.getInputStream(), UsernamePasswordRequest.class);
      // Build authentication from request
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          usernamePasswordRequest.getUsername(),
          usernamePasswordRequest.getPassword()
      ); // Perform authentication with authentication manager
      return authenticationManager.authenticate(authentication);
    } catch (IOException e) {
      log.error(String.format(
          "Exception while trying to access request input stream with message: %s",
          e.getMessage()));
      throw new RuntimeException("Could not access request data");
    } catch (AuthenticationException e) {
      unsuccessfulAuthentication(request, response, e);
      log.debug("Failed user login attempt");
      return null;
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) {
    Set<Privilege> privileges = authResult.getAuthorities()
        .stream() // Extract privileges from authentication result
        .filter(grantedAuthority -> grantedAuthority instanceof CustomGrantedAuthority)
        .map(grantedAuthority -> ((CustomGrantedAuthority) grantedAuthority).getPrivilege())
        .collect(Collectors.toSet());
    // Gets user data from auth result
    Object principal = authResult.getPrincipal();
    UserPrincipal userPrincipal = null;
    if (principal instanceof UserPrincipal) {
      userPrincipal = (UserPrincipal) principal;
    }
    User user = userPrincipal.getUser();
    String userName = authResult.getName();
    String token = jwtManager.createToken(userName, privileges); // Create token with privileges
    String refreshToken = refreshTokenService.createRefreshToken(userName);
    // Create login response with user, jwt and refresh token
    LoginResponse loginResponse = new LoginResponse(user, token, refreshToken);
    // Add token to response header
    response.addHeader(HttpHeaders.AUTHORIZATION,
        String.format("%s %s", jwtManager.getTokenPrefix(), token));
    try { // Writes login response to response body
      String responseBody = StringUtils.asJsonString(loginResponse);
      response.getWriter().write(responseBody);
    } catch (IOException e) {
      handleIoException();
    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) {
    MagentaException magentaException = new MagentaException(
        AppConstants.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN, LocalDateTime.now());
    String responseBody = StringUtils.asJsonString(magentaException);
    try {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write(responseBody);
    } catch (IOException e) {
      handleIoException();
    }
  }

  private void handleIoException() {
    log.error("Error during response writing operation");
    throw new IllegalStateException("Error during response writing operation");
  }
}
