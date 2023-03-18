package com.authprovider.dto;

import jakarta.servlet.http.Cookie;
import java.time.Duration;

public class SecureCookie extends Cookie {

  public static final String accessTokenKey = "access_token";
  public static final String refreshTokenKey = "refresh_token";

  public SecureCookie(String name, String value) {
    super(name, value);
    this.setHttpOnly(true);
    this.setPath("/");
    this.setSecure(false);
    this.setMaxAge((int) Duration.ofMinutes(20).toSeconds());
  }

  public SecureCookie delete() {
    this.setValue("");
    this.setMaxAge(0);
    return this;
  }
}
