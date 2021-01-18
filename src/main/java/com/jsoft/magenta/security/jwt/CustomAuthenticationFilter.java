package com.jsoft.magenta.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoft.magenta.exceptions.InvalidCredentialsException;
import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.payload.UsernamePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
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
                    "Exception while trying to access request input stream with message: %s", e.getMessage()));
            throw new RuntimeException("Could not access request data");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Either username or password are invalid");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult)
    {
       Set<Privilege> privileges = authResult.getAuthorities().stream() // Extract privileges from authentication result
               .filter(grantedAuthority -> grantedAuthority instanceof CustomGrantedAuthority)
               .map(grantedAuthority -> ((CustomGrantedAuthority) grantedAuthority).getPrivilege())
               .collect(Collectors.toSet());
        String token = jwtManager.createToken(authResult.getName(), privileges); // Create token with privileges
        // Add token to response header
        response.addHeader(HttpHeaders.AUTHORIZATION, String.format("%s %s", jwtManager.getTokenPrefix(), token));
    }
}
