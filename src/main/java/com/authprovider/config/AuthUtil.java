package com.authprovider.config;

import com.authprovider.dto.SecureCookie;
import com.authprovider.dto.UserResponseDTO;
import com.authprovider.service.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.jose4j.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class AuthUtil {

  @Autowired
  JWTService jwtService;

  public UserResponseDTO extractUser(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, SecureCookie.accessTokenKey);
    if (cookie == null) return null;
    try {
      JwtClaims claims = jwtService.extractClaims(cookie.getValue());
      return new UserResponseDTO(
        UUID.fromString(claims.getSubject()),
        claims.getClaimValueAsString("email")
      );
    } catch (Exception e) {
      return null;
    }
  }
}
