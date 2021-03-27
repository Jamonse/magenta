package com.jsoft.magenta.security.configuration;

import com.jsoft.magenta.security.jwt.CustomAuthenticationFilter;
import com.jsoft.magenta.security.jwt.JwtFilter;
import com.jsoft.magenta.security.jwt.JwtManager;
import com.jsoft.magenta.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${application.cors.allowed-origin}")
  private String allowedOrigin;

  private static final String API_URL = "/magenta/v1/";
  private static final String LOGIN_URL = API_URL + "login";
  private static final String LOGOUT_URL = API_URL + "auth/logout";
  private static final String REFRESH_URL = API_URL + "auth/refresh";

  private final JwtManager jwtManager;
  private final RefreshTokenService refreshTokenService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Custom CORS configuration
    http.cors();
    // Disable CSRF protection - JWT usage
    http.csrf().disable();
    // Disable session creation - JWT usage
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // Add custom UsernamePasswordAuthenticationFilter to return token upon successful authentication
    http.addFilter(authFilter());
    // Allow all requests to login and logout endpoint and authenticate all others
    http.authorizeRequests()
        .antMatchers(LOGIN_URL).permitAll()
        .antMatchers(LOGOUT_URL).permitAll()
        .antMatchers(REFRESH_URL).permitAll()
        .anyRequest().authenticated();
    // Add JWT filter before UsernamePasswordAuthenticationFilter
    http.addFilterBefore(new JwtFilter(jwtManager), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring()
        .antMatchers("/v2/api-docs")
        .antMatchers("/swagger-resources/**")
        .antMatchers("/swagger-ui.html")
        .antMatchers("/configuration/**")
        .antMatchers("/webjars/**")
        .antMatchers("/public");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(daoAuthenticationProvider());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(14);
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
  }

  @Bean
  public WebMvcConfigurer corsConfiguration() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/magenta/v1/**")
            .allowedOrigins(allowedOrigin);
      }
    };
  }

  private CustomAuthenticationFilter authFilter() throws Exception {
    CustomAuthenticationFilter authFilter = new CustomAuthenticationFilter(
        authenticationManager(), refreshTokenService, jwtManager);
    authFilter.setFilterProcessesUrl(LOGIN_URL);
    return authFilter;
  }
}
