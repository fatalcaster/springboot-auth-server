package com.authprovider.dto;

import jakarta.servlet.http.Cookie;
import java.time.Duration;

public class SecureCookie extends Cookie {

  public static final String accessTokenKey = "access_token";
  public static final String refreshTokenKey = "refresh_token";
  public static final String AuthorizationHeader = "Authorization";

  public SecureCookie(String name, String value) {
    super(name, value);
    this.setHttpOnly(false);
    this.setPath("/");
    this.setSecure(false);
    this.setMaxAge((int) Duration.ofDays(60).toSeconds());
  }

  public static SecureCookie delete(String key) {
    SecureCookie cookie = new SecureCookie(key, null);
    cookie.setMaxAge(0);
    return cookie;
  }
}
