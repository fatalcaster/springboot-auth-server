package com.authprovider.dto;

import java.time.Duration;
import org.springframework.http.ResponseCookie;

public class SecureCookie {

  public static final String REFRESH_TOKEN_KEY = "refresh_token";
  public static final String AUTHORIZATION_HEADER = "Authorization";

  private SecureCookie() {}

  public static ResponseCookie build(String key, String value) {
    return ResponseCookie
      .from(key, value)
      .secure(false)
      .httpOnly(true)
      .path("/")
      .maxAge((int) Duration.ofDays(60).toSeconds())
      .sameSite("Strict")
      .build();
  }
}
