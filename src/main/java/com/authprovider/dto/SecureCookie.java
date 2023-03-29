package com.authprovider.dto;

import jakarta.servlet.http.Cookie;
import java.time.Duration;

public class SecureCookie extends Cookie {

  public static final String refreshTokenKey = "refresh_token";
  public static final String AuthorizationHeader = "Authorization";

  public SecureCookie(String name, String value) {
    super(name, value);
    this.setHttpOnly(true);
    this.setPath("/");
    this.setSecure(true);
    this.setMaxAge((int) Duration.ofDays(60).toSeconds());
  }

  public static SecureCookie delete(String key) {
    SecureCookie cookie = new SecureCookie(key, null);
    cookie.setMaxAge(0);
    return cookie;
  }
}
