package com.authprovider.config;

import com.authprovider.dto.SecureCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
          .addMapping("/**")
          .allowedOrigins("http://localhost:3000")
          .allowCredentials(true)
          .allowedMethods("GET", "POST", "DELETE", "PUT");
      }
    };
  }

  @Bean
  public SecurityFilterChain filterChain(
    HttpSecurity http,
    @Autowired JwtAuthorizationFilter jwtAuthorizationFilter
  ) throws Exception {
    http.csrf().disable();
    http.cors();
    http
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http
      .authorizeHttpRequests()
      .requestMatchers(
        "/api/auth/**",
        "/api/oauth2/token",
        "/api/auth/refresh",
        "/api/auth/logout"
      )
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .addFilterBefore(
        jwtAuthorizationFilter,
        UsernamePasswordAuthenticationFilter.class
      );

    http.logout(logout ->
      logout
        .logoutUrl("/api/auth/logout")
        .addLogoutHandler(
          new CookieClearingLogoutHandler(
            SecureCookie.delete(SecureCookie.refreshTokenKey)
          )
        )
        .logoutSuccessHandler(
          (new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        )
        .deleteCookies(SecureCookie.refreshTokenKey)
        .logoutSuccessUrl("http://localhost:3000")
    );

    return http.build();
  }
}
