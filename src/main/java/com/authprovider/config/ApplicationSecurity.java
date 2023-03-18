package com.authprovider.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(
    HttpSecurity http,
    @Autowired JwtAuthorizationFilter jwtAuthorizationFilter
  ) throws Exception {
    http.csrf().disable();
    http
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http
      .authorizeHttpRequests()
      .requestMatchers(
        "/api/auth/**",
        "/api/roles",
        "/api/test",
        "/api/oauth2/token"
      )
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .addFilterBefore(
        jwtAuthorizationFilter,
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }
}
