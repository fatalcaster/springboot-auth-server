package com.authprovider.dto;

import jakarta.servlet.http.Cookie;
import java.time.Duration;

public class SecureCookie extends Cookie {

  public SecureCookie(String name, String value) {
    super(name, value);
    this.setHttpOnly(true);
    this.setPath("/");
    this.setSecure(false);
    this.setMaxAge((int) Duration.ofMinutes(20).toSeconds());
  }
}
